package com.igodating.dto.questionary.create;

import java.util.List;

public record AnswersCreateUpdateDto(
        Long questionaryId,
        List<AnswerCreateUpdateDto> answers
) {
}
