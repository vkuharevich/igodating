package com.igodating.questionary.scheduler;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserQuestionaryEmbeddingCalculationTaskScheduler {

    private final UserQuestionaryEmbeddingCalculationTask userQuestionaryEmbeddingCalculationTask;

    @Value("${task.user-questionary-embedding-calculation-task.batch-size}")
    private Integer batchSize;


    @Scheduled(cron = "${task.user-questionary-embedding-calculation-task.cron}")
    @SchedulerLock(name = "UserQuestionaryEmbeddingCalculationTask")
    public void scheduledTask() {
        userQuestionaryEmbeddingCalculationTask.executeEmbeddingCalculation(batchSize);
    }
}
