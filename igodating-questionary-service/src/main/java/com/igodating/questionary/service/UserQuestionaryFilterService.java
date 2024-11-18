package com.igodating.questionary.service;

import com.igodating.questionary.dto.CursorResponse;
import com.igodating.questionary.dto.filter.UserQuestionaryFilter;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryShortView;

public interface UserQuestionaryFilterService {
    CursorResponse<UserQuestionaryShortView> findByCursorForUserId(UserQuestionaryFilter filter, String userId);
}
