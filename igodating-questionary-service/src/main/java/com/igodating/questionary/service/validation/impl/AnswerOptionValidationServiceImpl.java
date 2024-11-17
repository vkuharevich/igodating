package com.igodating.questionary.service.validation.impl;

import com.igodating.questionary.exception.ValidationException;
import com.igodating.questionary.model.AnswerOption;
import com.igodating.questionary.service.validation.AnswerOptionValidationService;
import com.igodating.questionary.service.validation.AnswerValueFormatValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnswerOptionValidationServiceImpl implements AnswerOptionValidationService {

    private final AnswerValueFormatValidationService answerValueFormatValidationService;

    @Override
    @Transactional(readOnly = true)
    public void validateOnCreate(AnswerOption answerOption) {
        if (answerOption.getId() != null) {
            throw new ValidationException("Cannot create answer option with preset id");
        }

        answerValueFormatValidationService.validateMultipleValues(answerOption.getValue());
    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnUpdate(AnswerOption answerOption) {
        if (answerOption.getId() == null) {
            throw new ValidationException("Cannot update answer option without id");
        }

        answerValueFormatValidationService.validateMultipleValues(answerOption.getValue());
    }
}
