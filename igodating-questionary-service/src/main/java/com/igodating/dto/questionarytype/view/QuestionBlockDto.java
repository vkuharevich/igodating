package com.igodating.dto.questionarytype.view;

import java.util.List;

public record QuestionBlockDto(
        Long id,
        String name,
        String description,
        Integer order,
        List<QuestionDto> questions
) {
}
