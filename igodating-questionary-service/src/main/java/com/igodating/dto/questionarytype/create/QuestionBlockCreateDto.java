package com.igodating.dto.questionarytype.create;

import java.util.List;

public record QuestionBlockCreateDto(
        String name,
        String description,
        Integer order,
        List<QuestionCreateDto> questions
) {
}
