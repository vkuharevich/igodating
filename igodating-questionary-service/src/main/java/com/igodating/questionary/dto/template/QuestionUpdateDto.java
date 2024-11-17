package com.igodating.questionary.dto.template;

import java.util.List;

public record QuestionUpdateDto(
        Long id,
        String title,
        String description,
        List<AnswerOptionUpdateDto> answerOptions
) {
}
