package com.igodating.questionary.service;

import com.igodating.questionary.dto.CursorResponse;
import com.igodating.questionary.dto.filter.UserQuestionaryFilter;
import com.igodating.questionary.model.UserQuestionary;

public interface UserQuestionaryFilterService {
    CursorResponse<UserQuestionary> findByCursor(UserQuestionaryFilter filter);
}
