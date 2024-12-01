package com.igodating.questionary.service.validation;

import com.igodating.questionary.model.QuestionBlock;

public interface QuestionBlockValidationService {

    void validateOnCreate(QuestionBlock questionBlock);

    void validateOnUpdate(QuestionBlock questionBlock);
}
