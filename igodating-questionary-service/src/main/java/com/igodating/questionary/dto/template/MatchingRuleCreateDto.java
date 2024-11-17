package com.igodating.questionary.dto.template;

import com.igodating.questionary.model.constant.RuleAccessType;
import com.igodating.questionary.model.constant.RuleMatchingType;

public record MatchingRuleCreateDto(
        RuleMatchingType matchingType,
        String presetValue,
        RuleAccessType accessType
) {
}
