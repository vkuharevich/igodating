package com.igodating.dto.questionary.create;

public record AnswerCreateUpdateDto(
        Long questionId,
        String textValue,
        Double numericValue,
        String[] chosenOptions
) {
}
