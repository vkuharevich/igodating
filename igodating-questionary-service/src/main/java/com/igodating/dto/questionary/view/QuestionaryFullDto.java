package com.igodating.dto.questionary.view;

import com.igodating.dto.questionarytype.view.QuestionaryTypeFullDto;

import java.time.LocalDateTime;
import java.util.List;

public record QuestionaryFullDto(
        Long id,
        String name,
        QuestionaryTypeFullDto type,
        LocalDateTime createdAt,
        Long userId,
        List<AnswerDto> answers
) {
}
