package com.igodating.questionary.dto.template;

import com.igodating.questionary.dto.filter.FullTextSearchSettings;
import com.igodating.questionary.model.constant.RuleAccessType;
import com.igodating.questionary.model.constant.RuleMatchingType;

public record MatchingRuleCreateDto(
        RuleMatchingType matchingType,
        String presetValue,
        FullTextSearchSettings presetValueFullTextSearchSettings,
        RuleAccessType accessType,
        Boolean isMandatoryForMatching
) {
}
