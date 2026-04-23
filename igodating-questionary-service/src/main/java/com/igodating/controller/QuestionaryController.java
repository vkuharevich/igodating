package com.igodating.controller;

import com.igodating.dto.questionary.create.AnswersCreateUpdateDto;
import com.igodating.dto.questionary.create.QuestionaryCreateDto;
import com.igodating.dto.questionary.view.QuestionaryFullDto;
import com.igodating.dto.questionary.view.QuestionaryShortDto;
import com.igodating.dto.questionarytype.view.RecommendationView;
import com.igodating.dto.recommendations.request.RecommendationsRequest;
import com.igodating.service.QuestionaryService;
import com.igodating.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/questionary")
@RequiredArgsConstructor
public class QuestionaryController {

    private final RecommendationService recommendationService;

    private final QuestionaryService questionaryService;

    @PostMapping("/recommendations")
    public ResponseEntity<Slice<RecommendationView>> recommendations(@RequestBody RecommendationsRequest request) {
        return ResponseEntity.ok(recommendationService.getRecommendations(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionaryFullDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(questionaryService.getById(id));
    }

    @PostMapping("/answers")
    public ResponseEntity<QuestionaryShortDto> createOrUpdateAnswers(@RequestBody AnswersCreateUpdateDto answersCreateUpdateDto) {
        return ResponseEntity.ok(questionaryService.createOrUpdateAnswers(answersCreateUpdateDto));
    }

    @PostMapping("/publication/{id}")
    public ResponseEntity<QuestionaryShortDto> moveFromDraft(@PathVariable("id") Long id) {
        return ResponseEntity.ok(questionaryService.moveFromDraft(id));
    }

    @PostMapping
    public ResponseEntity<QuestionaryShortDto> createDraft(@RequestBody QuestionaryCreateDto questionaryCreateDto) {
        return ResponseEntity.ok(questionaryService.createDraft(questionaryCreateDto));
    }
}
