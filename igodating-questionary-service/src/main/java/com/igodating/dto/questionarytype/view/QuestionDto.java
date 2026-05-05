package com.igodating.dto.questionarytype.view;

import com.igodating.model.QuestionType;

import java.util.List;

public record QuestionDto(
        Long id,
        QuestionType type,
        String title,
        String description,
        Integer order,
        Boolean forSemanticSimilarity,
        Float weightCoefficientForVector,
        QuestionaryFilterDto filter,
        List<AnswerOptionDto> answerOptions
) {
}
