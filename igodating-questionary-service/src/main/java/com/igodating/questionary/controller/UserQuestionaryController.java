package com.igodating.questionary.controller;

import com.igodating.questionary.dto.SliceResponse;
import com.igodating.questionary.dto.filter.UserQuestionaryFilter;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryCreateRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryShortView;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryUpdateRequest;
import com.igodating.questionary.mapper.UserQuestionaryMapper;
import com.igodating.questionary.service.UserQuestionaryFilterService;
import com.igodating.questionary.service.UserQuestionaryService;
import com.igodating.questionary.util.CurrentUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserQuestionaryController {

    private final UserQuestionaryService userQuestionaryService;

    private final UserQuestionaryMapper userQuestionaryMapper;

    private final UserQuestionaryFilterService userQuestionaryFilterService;

    @MutationMapping
//    @Secured("ROLE_MANAGE_USER_QUESTIONARY")
    public Boolean createQuestionary(@Argument UserQuestionaryCreateRequest questionary) {
        userQuestionaryService.createDraft(userQuestionaryMapper.createRequestToModel(questionary), CurrentUserInfo.getUserId());
        return true;
    }

    @MutationMapping
//    @Secured("ROLE_MANAGE_USER_QUESTIONARY")
    public Boolean updateQuestionary(@Argument UserQuestionaryUpdateRequest questionary) {
        userQuestionaryService.update(userQuestionaryMapper.updateRequestToModel(questionary), CurrentUserInfo.getUserId());
        return true;
    }

    @MutationMapping
//    @Secured("ROLE_MANAGE_USER_QUESTIONARY")
    public Boolean deleteQuestionary(@Argument UserQuestionaryUpdateRequest questionary) {
        userQuestionaryService.delete(userQuestionaryMapper.updateRequestToModel(questionary), CurrentUserInfo.getUserId());
        return true;
    }

    @MutationMapping
//    @Secured("ROLE_MANAGE_USER_QUESTIONARY")
    public Boolean moveFromDraft(@Argument UserQuestionaryUpdateRequest questionary) {
        userQuestionaryService.moveFromDraft(userQuestionaryMapper.updateRequestToModel(questionary), CurrentUserInfo.getUserId());
        return true;
    }

    @QueryMapping
    public SliceResponse<UserQuestionaryShortView> filterQuestionaries(@Argument UserQuestionaryFilter request) {
        return new SliceResponse<>(userQuestionaryFilterService.findByFilter(request, CurrentUserInfo.getUserId()));
    }
}
