package com.igodating.questionary.service.validation;

import com.igodating.questionary.model.UserQuestionary;

public interface UserQuestionaryValidationService {

    void validateOnDelete(UserQuestionary userQuestionary);

    void validateOnSetStatusToPublished(UserQuestionary userQuestionary);

    void validateOnMoveFromDraft(UserQuestionary userQuestionary);

    void validateOnUpdateEmbedding(UserQuestionary userQuestionary);

    void validateOnCreate(UserQuestionary userQuestionary);

    void validateOnUpdate(UserQuestionary userQuestionary);
}
