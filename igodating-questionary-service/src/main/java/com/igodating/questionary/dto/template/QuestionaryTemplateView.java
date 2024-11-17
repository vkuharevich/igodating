package com.igodating.questionary.dto.template;

import java.util.List;

public record QuestionaryTemplateView(
        Long id,
        String name,
        String description,
        List<QuestionView> questions
) {
}
