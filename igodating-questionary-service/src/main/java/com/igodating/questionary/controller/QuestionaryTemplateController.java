package com.igodating.questionary.controller;

import com.igodating.questionary.dto.template.QuestionBlockCreateDto;
import com.igodating.questionary.dto.template.QuestionBlockUpdateDto;
import com.igodating.questionary.dto.template.QuestionBlockView;
import com.igodating.questionary.dto.template.QuestionView;
import com.igodating.questionary.dto.template.QuestionaryTemplateCreateRequest;
import com.igodating.questionary.dto.template.QuestionaryTemplateDeleteRequest;
import com.igodating.questionary.dto.template.QuestionaryTemplateUpdateRequest;
import com.igodating.questionary.dto.template.QuestionaryTemplateView;
import com.igodating.questionary.mapper.QuestionBlockMapper;
import com.igodating.questionary.mapper.QuestionMapper;
import com.igodating.questionary.mapper.QuestionaryTemplateMapper;
import com.igodating.questionary.service.QuestionaryTemplateService;
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
@RequestMapping("/api/questionary-template")
@RequiredArgsConstructor
@Slf4j
public class QuestionaryTemplateController {

    private final QuestionaryTemplateService questionaryTemplateService;

    private final QuestionaryTemplateMapper questionaryTemplateMapper;

    private final QuestionMapper questionMapper;

    private final QuestionBlockMapper questionBlockMapper;

    @GetMapping("/{id}")
    public ResponseEntity<QuestionaryTemplateView> questionaryTemplate(@PathVariable("id") Long id) {
        return ResponseEntity.ok(questionaryTemplateService.getById(id, questionaryTemplateMapper::modelToView));
    }

    @GetMapping("/question-block/{questionBlockId}")
    public ResponseEntity<List<QuestionView>> questionsFromBlock(@PathVariable("questionBlockId") Long questionBlockId) {
        return ResponseEntity.ok(questionaryTemplateService.getAllQuestionsFromBlock(questionBlockId, questionMapper::modelToView));
    }

    @GetMapping("/{id}/questions-without-block")
    public ResponseEntity<List<QuestionView>> questionsWithoutBlock(@PathVariable("id") Long templateId) {
        return ResponseEntity.ok(questionaryTemplateService.getAllQuestionsWithoutBlock(templateId, questionMapper::modelToView));
    }

    @GetMapping("/{id}/question-blocks")
    public ResponseEntity<List<QuestionBlockView>> questionBlocks(@PathVariable("id") Long templateId) {
        return ResponseEntity.ok(questionaryTemplateService.getAllQuestionBlocksByTemplateId(templateId, questionBlockMapper::modelToView));
    }

    @PostMapping
    public ResponseEntity<Long> createTemplate(@RequestBody QuestionaryTemplateCreateRequest template) {
        return ResponseEntity.ok(questionaryTemplateService.create(template, questionaryTemplateMapper::createRequestToModel));
    }

    @PutMapping
    public ResponseEntity<Long> updateTemplate(@RequestBody QuestionaryTemplateUpdateRequest template) {
        return ResponseEntity.ok(questionaryTemplateService.update(template, questionaryTemplateMapper::updateRequestToModel));
    }

    @DeleteMapping
    public ResponseEntity<Long> deleteTemplate(@RequestBody QuestionaryTemplateDeleteRequest template) {
        return ResponseEntity.ok(questionaryTemplateService.delete(template, questionaryTemplateMapper::deleteRequestToModel));
    }

    @PostMapping("/question-block")
    public ResponseEntity<Long> createQuestionBlock(@RequestBody QuestionBlockCreateDto questionBlock) {
        return ResponseEntity.ok(questionaryTemplateService.createQuestionBlock(questionBlock, questionBlockMapper::createRequestToModel));
    }

    @PutMapping("/question-block")
    public ResponseEntity<Long> updateQuestionBlock(@RequestBody QuestionBlockUpdateDto questionBlock) {
        return ResponseEntity.ok(questionaryTemplateService.updateQuestionBlock(questionBlock, questionBlockMapper::updateRequestToModel));
    }
}
