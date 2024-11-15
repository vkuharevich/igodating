package com.igodating.questionary.scheduler;

import com.igodating.questionary.dto.TextEmbeddingRequest;
import com.igodating.questionary.dto.TextEmbeddingRequestItem;
import com.igodating.questionary.dto.TextEmbeddingResponse;
import com.igodating.questionary.feign.TextEmbeddingService;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.model.UserQuestionaryAnswer;
import com.igodating.questionary.service.UserQuestionaryService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.igodating.questionary.model.constant.QuestionAnswerType.FREE_FORM;
import static com.igodating.questionary.model.constant.RuleMatchingType.SEMANTIC_RANGING;
import static com.igodating.questionary.model.constant.UserQuestionaryStatus.PROCESSED;

@Component
@RequiredArgsConstructor
public class UserQuestionaryEmbeddingCalculationTask {

    private final UserQuestionaryService userQuestionaryService;

    private final TextEmbeddingService textEmbeddingService;

    private ExecutorService executorService;

    @Value("${task.user-questionary-embedding-calculation-task.batch-size}")
    private Integer batchSize;

    @Value("${task.user-questionary-embedding-calculation-task.threads-count}")
    private Integer threadsCount;

    @PostConstruct
    public void init() {
        this.executorService = Executors.newFixedThreadPool(threadsCount);
    }

    @Scheduled(cron = "${task.user-questionary-embedding-calculation-task.cron}")
    @SchedulerLock(name = "UserQuestionaryEmbeddingCalculationTask", lockAtLeastFor = "PT5M", lockAtMostFor = "PT14M")
    public void scheduledTask() {
        List<UserQuestionary> questionaries = userQuestionaryService.findUnprocessedWithLimit(batchSize);

        for (UserQuestionary userQuestionary : questionaries) {
            executorService.submit(() -> handleQuestionary(userQuestionary));
        }
    }

    private void handleQuestionary(UserQuestionary questionary) {
        Map<Long, UserQuestionaryAnswer> answersIdMap = new HashMap<>();

        List<UserQuestionaryAnswer> answersInFreeFormAndSemanticMatchingRule = questionary.getAnswers()
                .stream()
                .filter(answer -> {
                    answersIdMap.put(answer.getId(), answer);
                    Question question = answer.getQuestion();
                    return FREE_FORM.equals(question.getAnswerType()) && question.getMatchingRule() != null && SEMANTIC_RANGING.equals(question.getMatchingRule().getMatchingType());
                })
                .toList();

        if (answersInFreeFormAndSemanticMatchingRule.isEmpty()) {
            questionary.setQuestionaryStatus(PROCESSED);
            userQuestionaryService.update(questionary);
            return;
        }

        List<TextEmbeddingRequestItem> requestItems = answersInFreeFormAndSemanticMatchingRule
                .stream()
                .map(answer -> new TextEmbeddingRequestItem(answer.getId().toString(), answer.getValue()))
                .toList();

        TextEmbeddingResponse response = textEmbeddingService.getEmbeddings(new TextEmbeddingRequest(requestItems));

        questionary.setEmbedding(response.globalEmbedding());
        questionary.setQuestionaryStatus(PROCESSED);
        response.sentences().forEach(resultItem -> {
            UserQuestionaryAnswer answer = answersIdMap.get(Long.parseLong(resultItem.sentenceId()));
            answer.setEmbedding(resultItem.embedding());
        });

        userQuestionaryService.update(questionary);
    }
}
