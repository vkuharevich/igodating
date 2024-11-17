package com.igodating.questionary.service.cache;

import com.igodating.questionary.model.QuestionaryTemplate;

public interface QuestionaryTemplateCacheService {

    QuestionaryTemplate getById(Long id);

    void load();
}
