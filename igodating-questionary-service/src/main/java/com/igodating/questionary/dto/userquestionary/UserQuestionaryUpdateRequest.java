package com.igodating.questionary.dto.userquestionary;

import java.util.List;

public record UserQuestionaryUpdateRequest(
        Long id,
        String title,
        String description,
        List<UserQuestionaryAnswerUpdateDto> answers
) {
}
