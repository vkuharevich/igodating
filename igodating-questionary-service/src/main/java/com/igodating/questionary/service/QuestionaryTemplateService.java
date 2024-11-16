package com.igodating.questionary.service;

import com.igodating.questionary.model.QuestionaryTemplate;

public interface QuestionaryTemplateService {

    void create(QuestionaryTemplate questionaryTemplate);

    void update(QuestionaryTemplate questionaryTemplate);

    void delete(QuestionaryTemplate questionaryTemplate);
}
