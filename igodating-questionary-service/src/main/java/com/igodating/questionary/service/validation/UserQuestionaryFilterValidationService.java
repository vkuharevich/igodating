package com.igodating.questionary.service.validation;

import com.igodating.questionary.dto.filter.UserQuestionaryFilter;

public interface UserQuestionaryFilterValidationService {

    void validateUserQuestionaryFilter(UserQuestionaryFilter filter, String userId);
}
