package com.igodating.dto.questionarytype.create;

import com.igodating.model.QuestionType;

import java.util.List;

public record QuestionCreateDto(
        QuestionType type,
        String title,
        String description,
        Integer order,
        Boolean forSemanticSimilarity,
        Float weightCoefficientForVector,
        QuestionaryFilterCreateDto filter,
        List<AnswerOptionCreateDto> answerOptions
) {
}
