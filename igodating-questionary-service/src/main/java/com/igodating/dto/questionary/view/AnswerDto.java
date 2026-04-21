package com.igodating.dto.questionary.view;

public record AnswerDto(
        Long id,
        String textValue,
        Double numericValue,
        Long questionId,
        String[] chosenOptions
) {
}
