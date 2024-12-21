package com.igodating.questionary.service.validation;

import com.igodating.questionary.dto.filter.UserQuestionaryRecommendationRequest;

public interface UserQuestionaryFilterValidationService {

    void validateUserQuestionaryFilter(UserQuestionaryRecommendationRequest filter, String userId);
}
