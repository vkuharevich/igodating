package com.igodating.dto.questionarytype.create;

import java.util.List;

public record QuestionaryTypeCreateDto(
        String name,
        String description,
        Float factorLambdaPart,
        List<QuestionBlockCreateDto> questionBlocks
) {
}
