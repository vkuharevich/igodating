package com.igodating.questionary.dto.template;

import java.util.List;

public record QuestionaryTemplateUpdateRequest(
        Long id,
        String name,
        String description,
        List<QuestionUpdateDto> questions
) {
}
