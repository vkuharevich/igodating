package com.igodating.questionary.dto.filter;

public record UserQuestionaryFilterItem(
    Long questionId,
    String filterValue,
    FullTextSearchSettings fullTextSearchSettings
) {

    public boolean isEmpty() {
        return filterValue == null && fullTextSearchSettings == null;
    }
}
