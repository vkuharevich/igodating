package com.igodating.questionary.service.validation;

import com.igodating.questionary.model.QuestionaryTemplate;

public interface QuestionaryTemplateValidationService {

    void validateOnCreate(QuestionaryTemplate questionaryTemplate);

    void validateOnUpdate(QuestionaryTemplate questionaryTemplate);

    void validateOnDelete(QuestionaryTemplate questionaryTemplate);
}
