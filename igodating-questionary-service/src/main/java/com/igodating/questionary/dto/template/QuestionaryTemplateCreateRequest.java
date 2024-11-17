package com.igodating.questionary.dto.template;

import java.util.List;

public record QuestionaryTemplateCreateRequest(
        String name,
        String description,
        List<QuestionCreateDto> questions
) {
}
