package com.igodating.questionary.dto.template;

import com.igodating.questionary.model.constant.QuestionAnswerType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record QuestionView(
        Long id,
        Long questionaryTemplateId,
        MatchingRuleView matchingRule,
        Long questionBlockId,
        QuestionBlockView questionBlock,
        String title,
        String description,
        QuestionAnswerType answerType,
        Boolean isMandatory,
        BigDecimal fromVal,
        BigDecimal toVal,
        LocalDateTime createdAt,
        List<String> answerOptions
) {
}
