package com.igodating.questionary.controller;

import com.igodating.commons.dto.ResponseWrapper;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Контроллер для пользовательских анкет", description = "Контроллер, отвечающий за бизнес-логику по пользовательским анкетам")
public class UserQuestionaryController {

    private final UserQuestionaryService userQuestionaryService;

    @GetMapping("/{id}")
    @Operation(summary = "Пользовательская анкета", description = "Получение пользовательской анкеты по ID")
    public ResponseWrapper<UserQuestionaryView> userQuestionary(@PathVariable("id") Long questionaryId) {
        return ResponseWrapper.ok(userQuestionaryService.getById(questionaryId));
    }

    @Operation(summary = "Рекомендации", description = "Получение рекомендаций по определенному запросу")
    @GetMapping("/recommendations")
    public ResponseWrapper<SliceResponse<UserQuestionaryRecommendation>> recommendations(UserQuestionaryRecommendationRequest request) {
        return ResponseWrapper.ok(new SliceResponse<>(userQuestionaryService.findRecommendations(request, CurrentUserInfo.getUserId())));
    }

    @Operation(summary = "Фильтры поиска", description = "Получение доступных фильтров поиска по ID шаблона")
    @GetMapping("/public-filters/{questionaryTemplateId}")
    public ResponseWrapper<List<PublicFilterDescriptorDto>> publicFilters(@PathVariable("questionaryTemplateId") Long questionaryTemplateId) {
        return ResponseWrapper.ok(userQuestionaryService.getAllAnswersMatchedWithPublicRulesByTemplateIdAndUserId(questionaryTemplateId, CurrentUserInfo.getUserId()));
    }

    @Operation(summary = "Создание пользовательской анкеты", description = "Создание пользовательской анкеты")
    @PostMapping
    public ResponseWrapper<Long> createQuestionary(@RequestBody UserQuestionaryCreateRequest questionary) {
        return ResponseWrapper.ok(userQuestionaryService.createDraft(questionary, CurrentUserInfo.getUserId()));
    }

    @Operation(summary = "Обновление пользовательской анкеты", description = "Обновление пользовательской анкеты")
    @PutMapping
    public ResponseWrapper<Long> updateQuestionary(@RequestBody UserQuestionaryUpdateRequest questionary) {
        return ResponseWrapper.ok(userQuestionaryService.update(questionary, CurrentUserInfo.getUserId()));
    }

    @Operation(summary = "Удаление пользовательской анкеты", description = "Удаление пользовательской анкеты")
    @DeleteMapping
    public ResponseWrapper<Long> deleteQuestionary(@RequestBody UserQuestionaryDeleteRequest questionary) {
        return ResponseWrapper.ok(userQuestionaryService.delete(questionary, CurrentUserInfo.getUserId()));
    }

    @Operation(summary = "Публикация пользовательской анкеты", description = "Публикация пользовательской анкеты")
    @PutMapping("/publish")
    public ResponseWrapper<Long> moveFromDraft(@RequestBody UserQuestionaryMoveFromDraftRequest questionary) {
        return ResponseWrapper.ok(userQuestionaryService.moveFromDraft(questionary, CurrentUserInfo.getUserId()));
    }
}
