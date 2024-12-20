package com.igodating.questionary.dto.template;

import com.igodating.questionary.dto.filter.FullTextSearchSettings;

public record MatchingRuleDefaultValueDto(
        String value,
        FullTextSearchSettings fullTextSearchSettings
) {
}
