package com.igodating.questionary.service.validation;

import com.igodating.questionary.model.Question;

public interface QuestionValidationService {

    void validateOnCreate(Question question);

    void validateOnUpdate(Question question);
}
