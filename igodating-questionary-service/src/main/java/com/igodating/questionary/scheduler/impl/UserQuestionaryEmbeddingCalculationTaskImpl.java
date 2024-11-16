package com.igodating.questionary.scheduler.impl;

import com.igodating.questionary.dto.TextEmbeddingRequest;
import com.igodating.questionary.dto.TextEmbeddingRequestItem;
import com.igodating.questionary.dto.TextEmbeddingResponse;
import com.igodating.questionary.feign.TextEmbeddingService;
import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.model.UserQuestionaryAnswer;
import com.igodating.questionary.model.constant.RuleMatchingType;
import com.igodating.questionary.scheduler.UserQuestionaryEmbeddingCalculationTask;
import com.igodating.questionary.service.UserQuestionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class UserQuestionaryEmbeddingCalculationTaskImpl implements UserQuestionaryEmbeddingCalculationTask {

    private final UserQuestionaryService userQuestionaryService;

    private final TextEmbeddingService textEmbeddingService;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void executeEmbeddingCalculation(int batchSize, int threadsCount) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(threadsCount)) {
            List<UserQuestionary> questionaries = userQuestionaryService.findUnprocessedWithLimit(batchSize);

            List<Future<?>> futures = new ArrayList<>();
            for (UserQuestionary userQuestionary : questionaries) {
                futures.add(executorService.submit(() -> handleQuestionary(userQuestionary)));
            }

            for (Future<?> future : futures) {
                try {
                    future.get();
                }  catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void handleQuestionary(UserQuestionary questionary) {
        Map<Long, UserQuestionaryAnswer> answersIdMap = new HashMap<>();

        List<UserQuestionaryAnswer> answersInFreeFormAndSemanticMatchingRule = questionary.getAnswers()
                .stream()
                .filter(answer -> {
                    answersIdMap.put(answer.getId(), answer);
                    Question question = answer.getQuestion();
                    MatchingRule matchingRule = question.getMatchingRule();
                    return matchingRule != null && RuleMatchingType.SEMANTIC_RANGING.equals(matchingRule.getMatchingType());
                })
                .toList();

        if (answersInFreeFormAndSemanticMatchingRule.isEmpty()) {
            userQuestionaryService.setStatusToPublished(questionary);
            return;
        }

        List<TextEmbeddingRequestItem> requestItems = answersInFreeFormAndSemanticMatchingRule
                .stream()
                .map(answer -> new TextEmbeddingRequestItem(answer.getId().toString(), answer.getValue()))
                .toList();

        TextEmbeddingResponse response = textEmbeddingService.getEmbeddings(new TextEmbeddingRequest(requestItems));

        questionary.setEmbedding(response.globalEmbedding());
        response.sentences().forEach(resultItem -> {
            UserQuestionaryAnswer answer = answersIdMap.get(Long.parseLong(resultItem.sentenceId()));
            answer.setEmbedding(resultItem.embedding());
        });

        userQuestionaryService.updateEmbeddingAndSetProcessed(questionary);
    }
}
