package com.igodating.dto.questionarytype.view;

import com.igodating.model.FilterType;

public record QuestionaryFilterDto(
        Long id,
        FilterType type,
        Boolean isPublic,
        String[] defaultOptionsInSetKeys,
        Float defaultNumericValueFrom,
        Float defaultNumericValueTo
) {
}
