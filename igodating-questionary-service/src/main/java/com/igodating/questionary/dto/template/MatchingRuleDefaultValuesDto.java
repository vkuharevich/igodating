package com.igodating.questionary.dto.template;

import java.util.List;

public record MatchingRuleDefaultValuesDto(
        List<MatchingRuleDefaultValuesCaseDto> cases,
        String defaultValue
) {
}
