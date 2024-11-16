package com.igodating.questionary.service.validation;

import com.igodating.questionary.model.constant.QuestionAnswerType;

public interface ValueFormatValidationService {

    void validateValueWithType(String value, QuestionAnswerType answerType);

    void validateMultipleValues(String value);
}
