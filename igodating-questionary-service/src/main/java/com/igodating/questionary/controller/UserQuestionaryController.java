package com.igodating.questionary.controller;

import com.igodating.questionary.dto.SliceResponse;
import com.igodating.questionary.dto.filter.UserQuestionaryRecommendationRequest;
import com.igodating.questionary.dto.template.PublicFilterDescriptorDto;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryCreateRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryRecommendation;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryUpdateRequest;
import com.igodating.questionary.mapper.UserQuestionaryAnswerMapper;
import com.igodating.questionary.mapper.UserQuestionaryMapper;
import com.igodating.questionary.service.UserQuestionaryRecommendationService;
import com.igodating.questionary.service.UserQuestionaryService;
import com.igodating.questionary.util.CurrentUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserQuestionaryController {

    private final UserQuestionaryService userQuestionaryService;

    private final UserQuestionaryMapper userQuestionaryMapper;

    private final UserQuestionaryAnswerMapper userQuestionaryAnswerMapper;

    private final UserQuestionaryRecommendationService userQuestionaryRecommendationService;

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
    public SliceResponse<UserQuestionaryRecommendation> recommendations(@Argument UserQuestionaryRecommendationRequest request) {
        return new SliceResponse<>(userQuestionaryRecommendationService.findRecommendations(request, CurrentUserInfo.getUserId()));
    }

    @QueryMapping
    public List<PublicFilterDescriptorDto> publicFilters(@Argument Long questionaryTemplateId) {
        return userQuestionaryService.getAllAnswersMatchedWithPublicRulesByTemplateIdAndUserId(questionaryTemplateId, CurrentUserInfo.getUserId())
                .stream()
                .map(userQuestionaryAnswerMapper::modelToPublicDescriptorDto)
                .toList();
    }
}
