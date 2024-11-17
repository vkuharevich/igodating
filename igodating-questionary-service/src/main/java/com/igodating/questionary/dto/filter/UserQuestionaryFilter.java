package com.igodating.questionary.dto.filter;

import java.util.List;

public record UserQuestionaryFilter(
        Long forUserQuestionaryId,
        List<UserQuestionaryFilterItem> userFilters,
        Long cursor,
        Integer limit
) {
}
