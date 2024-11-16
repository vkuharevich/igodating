package com.igodating.questionary.service.impl;

import com.igodating.questionary.model.QuestionaryTemplate;
import com.igodating.questionary.repository.QuestionRepository;
import com.igodating.questionary.repository.QuestionaryTemplateRepository;
import com.igodating.questionary.service.QuestionaryTemplateService;
import com.igodating.questionary.service.validation.QuestionaryTemplateValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionaryTemplateServiceImpl implements QuestionaryTemplateService {

    private final QuestionaryTemplateValidationService questionaryTemplateValidationService;

    private final QuestionaryTemplateRepository questionaryTemplateRepository;

    private final QuestionRepository questionRepository;

    @Override
    @Transactional
    public void create(QuestionaryTemplate questionaryTemplate) {
        questionaryTemplateValidationService.validateOnCreate(questionaryTemplate);
    }

    @Override
    @Transactional
    public void update(QuestionaryTemplate questionaryTemplate) {
        questionaryTemplateValidationService.validateOnUpdate(questionaryTemplate);
    }

    @Override
    @Transactional
    public void delete(QuestionaryTemplate questionaryTemplate) {
        questionaryTemplateValidationService.validateOnDelete(questionaryTemplate);
    }
}
