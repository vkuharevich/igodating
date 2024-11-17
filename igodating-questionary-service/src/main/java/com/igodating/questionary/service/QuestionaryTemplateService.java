package com.igodating.questionary.service;

import com.igodating.questionary.model.QuestionaryTemplate;

import java.util.List;

public interface QuestionaryTemplateService {

    QuestionaryTemplate getById(Long id);

    List<QuestionaryTemplate> getAll();

    void create(QuestionaryTemplate questionaryTemplate);

    void update(QuestionaryTemplate questionaryTemplate);

    void delete(QuestionaryTemplate questionaryTemplate);
}
