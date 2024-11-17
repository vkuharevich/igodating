package com.igodating.questionary.dto.userquestionary;

public record UserQuestionaryAnswerUpdateDto(
        Long id,
        Long questionId,
        String value
) {
}
