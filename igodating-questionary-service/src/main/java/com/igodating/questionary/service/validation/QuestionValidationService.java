package com.igodating.questionary.service.validation;

import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.QuestionaryTemplate;

public interface QuestionValidationService {

    void validateOnCreate(Question question);

    void validateOnUpdateWithQuestionaryTemplate(Question question, QuestionaryTemplate questionaryTemplate);
}
