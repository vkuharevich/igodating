package com.igodating.questionary.service.validation;

import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.model.UserQuestionaryAnswer;

public interface UserQuestionaryAnswerValidationService {

    void validateOnUpdateEmbedding(UserQuestionaryAnswer userQuestionaryAnswer);

    void validateOnCreate(UserQuestionaryAnswer userQuestionaryAnswer, UserQuestionary userQuestionary);

    void validateOnUpdate(UserQuestionaryAnswer userQuestionaryAnswer, UserQuestionary userQuestionary);
}
