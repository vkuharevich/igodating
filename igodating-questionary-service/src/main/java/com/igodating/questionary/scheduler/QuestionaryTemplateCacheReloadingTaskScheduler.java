package com.igodating.questionary.scheduler;

import com.igodating.questionary.scheduler.task.QuestionaryTemplateCacheReloadingTask;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuestionaryTemplateCacheReloadingTaskScheduler {

    private final QuestionaryTemplateCacheReloadingTask questionaryTemplateCacheReloadingTask;

    @Scheduled(cron = "${task.questionary-template-cache-reloading-task.cron}")
    @SchedulerLock(name = "QuestionaryTemplateCacheReloadingTask")
    public void scheduledTask() {
        questionaryTemplateCacheReloadingTask.executeCacheReloading();
    }
}
