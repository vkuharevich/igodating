package com.igodating.questionary.service;

import com.igodating.questionary.dto.filter.UserQuestionaryFilter;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryShortView;
import org.springframework.data.domain.Slice;

public interface UserQuestionaryFilterService {
    Slice<UserQuestionaryShortView> findByCursorForUserId(UserQuestionaryFilter filter, String userId);
}
