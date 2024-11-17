package com.igodating.questionary.dto.userquestionary;

import java.util.List;

public record UserQuestionaryCreateRequest(
        String title,
        String description,
        Long questionaryTemplateId,
        List<UserQuestionaryAnswerCreateDto> answers
) {
}
