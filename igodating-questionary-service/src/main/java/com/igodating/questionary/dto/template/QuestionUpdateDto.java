package com.igodating.questionary.dto.template;

import com.igodating.questionary.model.constant.QuestionAnswerType;

import java.math.BigDecimal;
import java.util.List;

public record QuestionUpdateDto(
        Long id,
        String title,
        String description,
        QuestionAnswerType answerType,
        Boolean isMandatory,
        BigDecimal fromVal,
        BigDecimal toVal,
        MatchingRuleUpdateDto matchingRule,
        List<AnswerOptionUpdateDto> answerOptions
) {
}
