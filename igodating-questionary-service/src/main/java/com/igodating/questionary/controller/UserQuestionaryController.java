package com.igodating.questionary.controller;

import com.igodating.questionary.dto.SliceResponse;
import com.igodating.questionary.dto.filter.UserQuestionaryRecommendationRequest;
import com.igodating.questionary.dto.template.PublicFilterDescriptorDto;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryCreateRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryDeleteRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryMoveFromDraftRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryRecommendation;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryUpdateRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryView;
import com.igodating.questionary.mapper.UserQuestionaryAnswerMapper;
import com.igodating.questionary.mapper.UserQuestionaryMapper;
import com.igodating.questionary.service.UserQuestionaryService;
import com.igodating.questionary.util.CurrentUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/questionary")
@RequiredArgsConstructor
@Slf4j
public class UserQuestionaryController {

    private final UserQuestionaryService userQuestionaryService;

    private final UserQuestionaryMapper userQuestionaryMapper;

    private final UserQuestionaryAnswerMapper userQuestionaryAnswerMapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserQuestionaryView> userQuestionary(@PathVariable("id") Long questionaryId) {
        return ResponseEntity.ok(userQuestionaryService.getById(questionaryId, userQuestionaryMapper::modelToView));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<SliceResponse<UserQuestionaryRecommendation>> recommendations(UserQuestionaryRecommendationRequest request) {
        return ResponseEntity.ok(new SliceResponse<>(userQuestionaryService.findRecommendations(request, CurrentUserInfo.getUserId(), userQuestionaryMapper::recommendationViewToDto)));
    }

    @GetMapping("/public-filters/{questionaryTemplateId}")
    public ResponseEntity<List<PublicFilterDescriptorDto>> publicFilters(@PathVariable("questionaryTemplateId") Long questionaryTemplateId) {
        return ResponseEntity.ok(userQuestionaryService.getAllAnswersMatchedWithPublicRulesByTemplateIdAndUserId(questionaryTemplateId, CurrentUserInfo.getUserId(), userQuestionaryAnswerMapper::modelToPublicDescriptorDto));
    }

    @PostMapping
    public ResponseEntity<Long> createQuestionary(@RequestBody UserQuestionaryCreateRequest questionary) {
        return ResponseEntity.ok(userQuestionaryService.createDraft(questionary, CurrentUserInfo.getUserId(), userQuestionaryMapper::createRequestToModel));
    }

    @PutMapping
    public ResponseEntity<Long> updateQuestionary(@RequestBody UserQuestionaryUpdateRequest questionary) {
        return ResponseEntity.ok(userQuestionaryService.update(questionary, CurrentUserInfo.getUserId(), userQuestionaryMapper::updateRequestToModel));
    }

    @DeleteMapping
    public ResponseEntity<Long> deleteQuestionary(@RequestBody UserQuestionaryDeleteRequest questionary) {
        return ResponseEntity.ok(userQuestionaryService.delete(questionary, CurrentUserInfo.getUserId(), userQuestionaryMapper::deleteRequestToModel));
    }

    @PutMapping("/publish")
    public ResponseEntity<Long> moveFromDraft(@RequestBody UserQuestionaryMoveFromDraftRequest questionary) {
        return ResponseEntity.ok(userQuestionaryService.moveFromDraft(questionary, CurrentUserInfo.getUserId(), userQuestionaryMapper::moveFromDraftRequestToModel));
    }
}
