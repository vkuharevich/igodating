package com.igodating.questionary.service.impl;

import com.igodating.questionary.dto.CursorResponse;
import com.igodating.questionary.dto.filter.UserQuestionaryFilter;
import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.repository.UserQuestionaryRepository;
import com.igodating.questionary.service.UserQuestionaryFilterService;
import com.igodating.questionary.service.cache.QuestionaryTemplateCacheService;
import com.igodating.questionary.service.validation.UserQuestionaryFilterValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserQuestionaryFilterServiceImpl implements UserQuestionaryFilterService {

    private final UserQuestionaryRepository userQuestionaryRepository;

    private final QuestionaryTemplateCacheService questionaryTemplateCacheService;

    private final UserQuestionaryFilterValidationService userQuestionaryFilterValidationService;


    @Override
    @Transactional(readOnly = true)
    public CursorResponse<UserQuestionary> findByCursorForUserId(UserQuestionaryFilter filter, String userId) {
        userQuestionaryFilterValidationService.validateUserQuestionaryFilter(filter, userId);

        return null;
    }
}
