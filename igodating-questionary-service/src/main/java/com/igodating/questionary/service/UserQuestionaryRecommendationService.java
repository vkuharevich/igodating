package com.igodating.questionary.service;

import com.igodating.questionary.dto.filter.UserQuestionaryFilter;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryRecommendation;
import org.springframework.data.domain.Slice;

public interface UserQuestionaryRecommendationService {
    Slice<UserQuestionaryRecommendation> findRecommendations(UserQuestionaryFilter filter, String userId);
}
