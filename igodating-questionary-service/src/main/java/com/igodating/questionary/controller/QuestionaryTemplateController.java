package com.igodating.questionary.controller;

import com.igodating.questionary.dto.template.QuestionaryTemplateCreateRequest;
import com.igodating.questionary.dto.template.QuestionaryTemplateUpdateRequest;
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

    @MutationMapping
    @Secured("ROLE_MANAGE_QUESTIONARY_TEMPLATE")
    public Boolean createTemplate(@Argument QuestionaryTemplateCreateRequest template) {
        questionaryTemplateService.create(questionaryTemplateMapper.createRequestToModel(template));
        return true;
    }

    @MutationMapping
    @Secured("ROLE_MANAGE_QUESTIONARY_TEMPLATE")
    public Boolean updateTemplate(@Argument QuestionaryTemplateUpdateRequest template) {
        questionaryTemplateService.update(questionaryTemplateMapper.updateRequestToModel(template));
        return true;
    }

    @MutationMapping
    @Secured("ROLE_MANAGE_QUESTIONARY_TEMPLATE")
    public Boolean deleteTemplate(@Argument QuestionaryTemplateUpdateRequest template) {
        questionaryTemplateService.delete(questionaryTemplateMapper.updateRequestToModel(template));
        return true;
    }

    @QueryMapping
    @Secured("ROLE_VIEW_QUESTIONARY_TEMPLATE")
    public QuestionAnswerType[] answerTypes() {
        return QuestionAnswerType.values();
    }
}
