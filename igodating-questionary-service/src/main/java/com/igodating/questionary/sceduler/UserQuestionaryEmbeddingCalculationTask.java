package com.igodating.questionary.sceduler;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserQuestionaryEmbeddingCalculationTask {

    @Value("${task.user-questionary-embedding-calculation-task.batch-size}")
    private Long batchSize;

    @Value("${task.user-questionary-embedding-calculation-task.threads-count}")
    private Long threadsCount;

    @Scheduled(cron = "${task.user-questionary-embedding-calculation-task.cron}")
    @SchedulerLock(name = "UserQuestionaryEmbeddingCalculationTask", lockAtLeastFor = "PT5M", lockAtMostFor = "PT14M")
    public void scheduledTask() {

    }
}
