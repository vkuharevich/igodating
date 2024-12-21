package com.igodating.questionary.dto.template;

import com.igodating.questionary.model.constant.QuestionAnswerType;
import com.igodating.questionary.model.constant.RuleMatchingType;

public record PublicFilterDescriptorDto(
        Long ruleId,
        Long questionId,
        RuleMatchingType matchingType,
        String defaultValue,
        QuestionAnswerType answerType,
        String questionBlockName
) {
}
