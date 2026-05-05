package com.igodating.dto.questionarytype.view;

public record QuestionaryTypeShortDto(
        Long id,
        String name,
        String description,
        Float factorLambdaPart,
        Float semanticLambdaPart
) {
}
