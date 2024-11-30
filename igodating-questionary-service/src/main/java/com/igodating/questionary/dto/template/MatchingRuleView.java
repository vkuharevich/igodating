package com.igodating.questionary.dto.template;

import com.igodating.questionary.dto.filter.FullTextSearchSettings;
import com.igodating.questionary.model.constant.RuleAccessType;
import com.igodating.questionary.model.constant.RuleMatchingType;

import java.time.LocalDateTime;

public record MatchingRuleView(
        Long id,
        Long questionId,
        RuleMatchingType matchingType,
        String presetValue,
        FullTextSearchSettings presetValueFullTextSearchSettings,
        RuleAccessType accessType,
        Boolean isMandatoryForMatching,
        LocalDateTime createdAt
) {
}
