package com.igodating.dto.questionarytype.view;

import java.util.List;

public record QuestionaryTypeFullDto(
        Long id,
        String name,
        String description,
        Float factorLambdaPart,
        Float semanticLambdaPart,
        List<QuestionBlockDto> questionBlocks
) {
}
