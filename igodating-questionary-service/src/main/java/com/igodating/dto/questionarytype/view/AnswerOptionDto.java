package com.igodating.dto.questionarytype.view;

public record AnswerOptionDto(
        Long id,
        String key,
        String value,
        Integer order,
        float[] influence
) {
}
