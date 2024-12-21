package com.igodating.questionary.dto.filter;

import org.apache.commons.lang3.StringUtils;

public record UserQuestionaryFilterItem(
    Long questionId,
    String filterValue
) {

    public boolean isEmpty() {
        return StringUtils.isEmpty(filterValue);
    }
}
