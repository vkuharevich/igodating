package com.igodating.questionary.service.impl;

import com.igodating.questionary.dto.CursorResponse;
import com.igodating.questionary.dto.filter.UserQuestionaryFilter;
import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.service.UserQuestionaryFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserQuestionaryFilterServiceImpl implements UserQuestionaryFilterService {

    @Override
    @Transactional(readOnly = true)
    public CursorResponse<UserQuestionary> findByCursor(UserQuestionaryFilter filter) {
        return null;
    }
}
