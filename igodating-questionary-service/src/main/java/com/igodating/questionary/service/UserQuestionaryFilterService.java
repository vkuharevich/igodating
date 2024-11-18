package com.igodating.questionary.service;

import com.igodating.questionary.dto.CursorResponse;
import com.igodating.questionary.dto.filter.UserQuestionaryFilter;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryShortView;
import org.springframework.data.domain.Page;

public interface UserQuestionaryFilterService {
    Page<UserQuestionaryShortView> findByCursorForUserId(UserQuestionaryFilter filter, String userId);
}
