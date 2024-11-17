package com.igodating.questionary.service.cache.impl;

import com.igodating.questionary.model.QuestionaryTemplate;
import com.igodating.questionary.service.QuestionaryTemplateService;
import com.igodating.questionary.service.cache.QuestionaryTemplateCacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionaryTemplateCacheServiceImpl implements QuestionaryTemplateCacheService {

    private final QuestionaryTemplateService questionaryTemplateService;

    private Map<Long, QuestionaryTemplate> cachedTemplates = new WeakHashMap<>();

    @PostConstruct
    public void init() {
        load();
    }

    @Override
    public QuestionaryTemplate getById(Long id) {
        return cachedTemplates.get(id);
    }

    @Override
    public void load() {
        cachedTemplates = questionaryTemplateService.getAll().stream().collect(Collectors.toMap(QuestionaryTemplate::getId, v -> v));
    }
}
