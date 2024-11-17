package com.igodating.questionary.dto.userquestionary;

import com.igodating.questionary.dto.template.QuestionView;

import java.time.LocalDateTime;

public record UserQuestionaryAnswerView(
        Long id,
        Long questionId,
        Long userQuestionaryId,
        QuestionView question,
        String value,
        LocalDateTime createdAt
) {
}
