package com.igodating.questionary.dto.template;

import com.igodating.questionary.model.constant.RuleAccessType;
import com.igodating.questionary.model.constant.RuleMatchingType;

public record MatchingRuleUpdateDto(
        Long id,
        RuleMatchingType matchingType,
        MatchingRuleDefaultValuesDto defaultValues,
        RuleAccessType accessType,
        Boolean isMandatoryForMatching
) {
}
