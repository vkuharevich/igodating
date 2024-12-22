package com.igodating.questionary.controller;

import com.igodating.questionary.dto.template.QuestionBlockCreateDto;
import com.igodating.questionary.dto.template.QuestionBlockUpdateDto;
import com.igodating.questionary.dto.template.QuestionaryTemplateCreateRequest;
import com.igodating.questionary.dto.template.QuestionaryTemplateUpdateRequest;
import com.igodating.questionary.mapper.QuestionBlockMapper;
import com.igodating.questionary.mapper.QuestionaryTemplateMapper;
import com.igodating.questionary.model.constant.QuestionAnswerType;
import com.igodating.questionary.service.QuestionaryTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class QuestionaryTemplateController {

    private final QuestionaryTemplateService questionaryTemplateService;

    private final QuestionaryTemplateMapper questionaryTemplateMapper;

    private final QuestionBlockMapper questionBlockMapper;

    @MutationMapping
//    @Secured("ROLE_MANAGE_QUESTIONARY_TEMPLATE")
    public Long createTemplate(@Argument QuestionaryTemplateCreateRequest template) {
        return questionaryTemplateService.create(questionaryTemplateMapper.createRequestToModel(template));
    }

    @MutationMapping
//    @Secured("ROLE_MANAGE_QUESTIONARY_TEMPLATE")
    public Long updateTemplate(@Argument QuestionaryTemplateUpdateRequest template) {
        return questionaryTemplateService.update(questionaryTemplateMapper.updateRequestToModel(template));
    }

    @MutationMapping
//    @Secured("ROLE_MANAGE_QUESTIONARY_TEMPLATE")
    public Long deleteTemplate(@Argument QuestionaryTemplateUpdateRequest template) {
        return questionaryTemplateService.delete(questionaryTemplateMapper.updateRequestToModel(template));
    }

    @MutationMapping
    public Long createQuestionBlock(@Argument QuestionBlockCreateDto questionBlock) {
        return questionaryTemplateService.createQuestionBlock(questionBlockMapper.createRequestToModel(questionBlock));
    }

    @MutationMapping
    public Long updateQuestionBlock(@Argument QuestionBlockUpdateDto questionBlock) {
        return questionaryTemplateService.updateQuestionBlock(questionBlockMapper.updateRequestToModel(questionBlock));
    }

    @QueryMapping
//    @Secured("ROLE_VIEW_QUESTIONARY_TEMPLATE")
    public QuestionAnswerType[] answerTypes() {
        return QuestionAnswerType.values();
    }
}
