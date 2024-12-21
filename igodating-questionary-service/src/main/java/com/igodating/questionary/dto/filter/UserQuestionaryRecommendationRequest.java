package com.igodating.questionary.dto.filter;

import java.util.List;

public record UserQuestionaryRecommendationRequest(
        Long forUserQuestionaryId,
        List<UserQuestionaryFilterItem> userFilters,
        Integer limit,
        Integer offset
) {
}
