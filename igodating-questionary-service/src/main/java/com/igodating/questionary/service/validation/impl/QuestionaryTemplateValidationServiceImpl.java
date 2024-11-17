package com.igodating.questionary.service.validation.impl;

import com.igodating.questionary.exception.ValidationException;
import com.igodating.questionary.model.QuestionaryTemplate;
import com.igodating.questionary.repository.QuestionaryTemplateRepository;
import com.igodating.questionary.service.validation.QuestionValidationService;
import com.igodating.questionary.service.validation.QuestionaryTemplateValidationService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class QuestionaryTemplateValidationServiceImpl implements QuestionaryTemplateValidationService {

    private final QuestionaryTemplateRepository questionaryTemplateRepository;

    private final QuestionValidationService questionValidationService;

    @Override
    @Transactional(readOnly = true)
    public void validateOnCreate(QuestionaryTemplate questionaryTemplate) {
        if (questionaryTemplate.getId() != null) {
            throw new ValidationException("Cannot create template with preset id");
        }

        checkCommonRequiredFieldsForCreateAndUpdateInTemplate(questionaryTemplate);

        questionaryTemplate.getQuestions().forEach(questionValidationService::validateOnCreate);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnUpdate(QuestionaryTemplate questionaryTemplate) {
        checkCommonRequiredFieldsForCreateAndUpdateInTemplate(questionaryTemplate);

        checkQuestionaryTemplateOnExistenceAndThrowIfDeleted(questionaryTemplate);

        questionaryTemplate.getQuestions().forEach(question -> {
            if (question.getId() == null) {
                questionValidationService.validateOnCreate(question);
            } else {
                questionValidationService.validateOnUpdate(question);
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnDelete(QuestionaryTemplate questionaryTemplate) {
        checkQuestionaryTemplateOnExistenceAndThrowIfDeleted(questionaryTemplate);
    }

    private void checkCommonRequiredFieldsForCreateAndUpdateInTemplate(QuestionaryTemplate questionaryTemplate) {
        if (StringUtils.isBlank(questionaryTemplate.getName())) {
            throw new ValidationException("Name is required for template");
        }

        if (StringUtils.isBlank(questionaryTemplate.getDescription())) {
            throw new ValidationException("Description is required for template");
        }

        if (CollectionUtils.isEmpty(questionaryTemplate.getQuestions())) {
            throw new ValidationException("Questions is required for template");
        }
    }

    private void checkQuestionaryTemplateOnExistenceAndThrowIfDeleted(QuestionaryTemplate questionaryTemplate) {
        Long id = questionaryTemplate.getId();
        if (id == null) {
            throw new ValidationException("Id is required for questionary updating");
        }

        questionaryTemplateRepository.findById(id).orElseThrow(() -> new ValidationException(String.format("Questionary template doesn't exist by id %d", id)));
    }
}
