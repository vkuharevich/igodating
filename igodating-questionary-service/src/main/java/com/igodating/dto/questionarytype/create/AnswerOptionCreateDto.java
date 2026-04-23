package com.igodating.dto.questionarytype.create;

public record AnswerOptionCreateDto(
        String value,
        String key,
        Integer order,
        Float[] influence
) {
}
