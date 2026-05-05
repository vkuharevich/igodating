package com.igodating.service.validation;

import com.igodating.dto.questionarytype.create.QuestionaryTypeCreateDto;

public interface QuestionaryTypeValidationService {

    void validateOnCreate(QuestionaryTypeCreateDto dto);
}
