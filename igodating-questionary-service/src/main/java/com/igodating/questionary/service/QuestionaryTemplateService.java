package com.igodating.questionary.service;

import com.igodating.questionary.dto.template.QuestionBlockCreateDto;
import com.igodating.questionary.dto.template.QuestionBlockUpdateDto;
import com.igodating.questionary.dto.template.QuestionBlockView;
import com.igodating.questionary.dto.template.QuestionView;
import com.igodating.questionary.dto.template.QuestionaryTemplateCreateRequest;
import com.igodating.questionary.dto.template.QuestionaryTemplateDeleteRequest;
import com.igodating.questionary.dto.template.QuestionaryTemplateUpdateRequest;
import com.igodating.questionary.dto.template.QuestionaryTemplateView;

import java.util.List;

public interface QuestionaryTemplateService {

    QuestionaryTemplateView getById(Long id);

    List<QuestionView> getAllQuestionsFromBlock(Long questionBlockId);

    List<QuestionView> getAllQuestionsWithoutBlock(Long questionTemplateId);

    List<QuestionBlockView> getAllQuestionBlocksByTemplateId(Long templateId);

    List<QuestionaryTemplateView> getAll();

    Long create(QuestionaryTemplateCreateRequest questionaryTemplateCreateRequest);

    Long update(QuestionaryTemplateUpdateRequest questionaryTemplateUpdateRequest);

    Long createQuestionBlock(QuestionBlockCreateDto questionBlockCreateRequest);

    Long updateQuestionBlock(QuestionBlockUpdateDto questionBlockUpdateRequest);

    Long delete(QuestionaryTemplateDeleteRequest questionaryTemplateDeleteRequest);
}
