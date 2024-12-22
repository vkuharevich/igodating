package com.igodating.questionary.repository;

import com.igodating.questionary.constant.SimilarityCalculatingOperator;
import com.igodating.questionary.dto.filter.UserQuestionaryFilterItem;
import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.model.view.UserQuestionaryRecommendationView;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ExtendedUserQuestionaryRepository {

    Slice<UserQuestionaryRecommendationView> findRecommendations(UserQuestionary forQuestionary, List<UserQuestionaryFilterItem> userFilters, SimilarityCalculatingOperator similarityCalculatingOperator, int limit, int offset);
}
