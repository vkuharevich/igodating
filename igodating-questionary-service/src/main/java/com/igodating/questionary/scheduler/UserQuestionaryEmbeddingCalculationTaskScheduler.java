package com.igodating.questionary.scheduler;

import com.igodating.questionary.dto.TextEmbeddingRequest;
import com.igodating.questionary.dto.TextEmbeddingRequestItem;
import com.igodating.questionary.dto.TextEmbeddingResponse;
import com.igodating.questionary.feign.TextEmbeddingService;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.model.UserQuestionaryAnswer;
import com.igodating.questionary.service.UserQuestionaryService;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
public class UserQuestionaryEmbeddingCalculationTaskScheduler {

    private final UserQuestionaryEmbeddingCalculationTask userQuestionaryEmbeddingCalculationTask;

    @Value("${task.user-questionary-embedding-calculation-task.batch-size}")
    private Integer batchSize;

    @Value("${task.user-questionary-embedding-calculation-task.threads-count}")
    private Integer threadsCount;


    @Scheduled(cron = "${task.user-questionary-embedding-calculation-task.cron}")
    @SchedulerLock(name = "UserQuestionaryEmbeddingCalculationTask", lockAtLeastFor = "PT5M", lockAtMostFor = "PT14M")
    public void scheduledTask() throws ExecutionException, InterruptedException {
        userQuestionaryEmbeddingCalculationTask.executeEmbeddingCalculation(batchSize, threadsCount);
    }
}
