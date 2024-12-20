package com.igodating.questionary.dto.template;

import com.igodating.questionary.dto.filter.FullTextSearchSettings;
import com.igodating.questionary.model.MatchingRuleDefaultValues;
import com.igodating.questionary.model.constant.RuleAccessType;
import com.igodating.questionary.model.constant.RuleMatchingType;

public record MatchingRuleUpdateDto(
        Long id,
        RuleMatchingType matchingType,
        MatchingRuleDefaultValuesDto defaultValues,
        FullTextSearchSettings presetValueFullTextSearchSettings,
        RuleAccessType accessType,
        Boolean isMandatoryForMatching
) {
}
