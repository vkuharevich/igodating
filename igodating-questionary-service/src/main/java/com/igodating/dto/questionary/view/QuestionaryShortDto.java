package com.igodating.dto.questionary.view;

import com.igodating.dto.questionarytype.view.QuestionaryTypeShortDto;
import com.igodating.model.QuestionaryStatus;

import java.time.LocalDateTime;

public record QuestionaryShortDto(
        Long id,
        String name,
        QuestionaryStatus status,
        QuestionaryTypeShortDto type,
        LocalDateTime createdAt,
        Long userId
) {
}
