package com.igodating.questionary.service.validation;

import com.igodating.questionary.model.AnswerOption;

public interface AnswerOptionValidationService {

    void validateOnCreate(AnswerOption answerOption);

    void validateOnUpdate(AnswerOption answerOption);
}
