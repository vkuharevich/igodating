package com.igodating.dto.questionarytype.create;

import com.igodating.model.FilterType;

public record QuestionaryFilterCreateDto(
        FilterType type,
        Boolean isPublic,
        String[] defaultOptionsInSetKeys,
        Float defaultNumericValueFrom,
        Float defaultNumericValueTo
) {
}
