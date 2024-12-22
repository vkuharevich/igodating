package com.igodating.questionary.service.validation;

import com.igodating.questionary.dto.filter.UserQuestionaryRecommendationRequest;
import com.igodating.questionary.model.UserQuestionary;

public interface UserQuestionaryValidationService {

    void validateUserQuestionaryFilter(UserQuestionaryRecommendationRequest filter, String userId);

    void validateOnDelete(UserQuestionary userQuestionary, String userId);

    void validateOnSetStatusToPublished(UserQuestionary userQuestionary);

    void validateOnMoveFromDraft(UserQuestionary userQuestionary, String userId);

    void validateOnUpdateEmbedding(UserQuestionary userQuestionary);

    void validateOnCreate(UserQuestionary userQuestionary);

    void validateOnUpdate(UserQuestionary userQuestionary, String userId);
}
