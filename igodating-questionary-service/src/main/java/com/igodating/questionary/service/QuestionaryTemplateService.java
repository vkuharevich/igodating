package com.igodating.questionary.service;

import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.QuestionBlock;
import com.igodating.questionary.model.QuestionaryTemplate;

import java.util.List;
import java.util.function.Function;

public interface QuestionaryTemplateService {

    <T> T getById(Long id, Function<QuestionaryTemplate, T> mappingFunc);

    <T> List<T> getAllQuestionsFromBlock(Long questionBlockId, Function<Question, T> mappingFunc);

    <T> List<T> getAllQuestionsWithoutBlock(Long questionTemplateId, Function<Question, T> mappingFunc);

    <T> List<T> getAllQuestionBlocksByTemplateId(Long templateId, Function<QuestionBlock, T> mappingFunc);

    <T> List<T> getAll(Function<QuestionaryTemplate, T> mappingFunc);

    <T> Long create(T questionaryTemplateCreateRequest, Function<T, QuestionaryTemplate> mappingFunc);

    <T> Long update(T questionaryTemplateUpdateRequest, Function<T, QuestionaryTemplate> mappingFunc);

    <T> Long createQuestionBlock(T questionBlockCreateRequest, Function<T, QuestionBlock> mappingFunc);

    <T> Long updateQuestionBlock(T questionBlockUpdateRequest, Function<T, QuestionBlock> mappingFunc);

    <T> Long delete(T questionaryTemplateDeleteRequest, Function<T, QuestionaryTemplate> mappingFunc);
}
