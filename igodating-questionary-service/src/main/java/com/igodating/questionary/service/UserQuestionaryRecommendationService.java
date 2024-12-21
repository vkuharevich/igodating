package com.igodating.questionary.service;

import com.igodating.questionary.dto.filter.UserQuestionaryRecommendationRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryRecommendation;
import org.springframework.data.domain.Slice;

public interface UserQuestionaryRecommendationService {
    Slice<UserQuestionaryRecommendation> findRecommendations(UserQuestionaryRecommendationRequest filter, String userId);
}
