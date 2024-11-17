package com.igodating.questionary.service.validation;

import com.igodating.questionary.model.Question;

public interface AnswerValueFormatValidationService {

    void validateValueWithQuestion(String value, Question question);

    void validateMultipleValues(String value);
}
