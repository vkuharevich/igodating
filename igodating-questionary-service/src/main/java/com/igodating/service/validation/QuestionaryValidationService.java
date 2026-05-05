package com.igodating.service.validation;

import com.igodating.dto.questionary.create.AnswersCreateUpdateDto;
import com.igodating.dto.questionary.create.QuestionaryCreateDto;
import com.igodating.model.Questionary;

public interface QuestionaryValidationService {

    void validateOnCreate(QuestionaryCreateDto questionaryCreateDto);

    void validateOnMovingOnDraft(Questionary questionary);

    void validateAnswers(Questionary questionary, AnswersCreateUpdateDto answersCreateUpdateDto);
}
