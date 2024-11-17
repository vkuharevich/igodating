package com.igodating.questionary.scheduler.task.impl;

import com.igodating.questionary.scheduler.task.QuestionaryTemplateCacheReloadingTask;
import com.igodating.questionary.service.cache.QuestionaryTemplateCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionaryTemplateCacheReloadingTaskImpl implements QuestionaryTemplateCacheReloadingTask {

    private final QuestionaryTemplateCacheService questionaryTemplateCacheService;

    @Override
    public void executeCacheReloading() {
        questionaryTemplateCacheService.load();
    }
}
