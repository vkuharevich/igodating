package com.igodating.questionary.service;

import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.QuestionBlock;
import com.igodating.questionary.model.QuestionaryTemplate;

import java.util.List;
import java.util.function.Function;

public interface QuestionaryTemplateService {

    <T> T getById(Long id, Function<QuestionaryTemplate, T> mappingFunc);

    <T> List<T> getAllQuestionsFromBlock(Long questionBlockId, Function<Question, T> mappingFunc);

    <T> List<T> getAllQuestionBlocksByTemplateId(Long templateId, Function<QuestionBlock, T> mappingFunc);

    <T> List<T> getAll(Function<QuestionaryTemplate, T> mappingFunc);

    void create(QuestionaryTemplate questionaryTemplate);

    void update(QuestionaryTemplate questionaryTemplate);

    void createQuestionBlock(QuestionBlock questionBlock);

    void updateQuestionBlock(QuestionBlock questionBlock);

    void delete(QuestionaryTemplate questionaryTemplate);
}
