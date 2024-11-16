package com.igodating.questionary.service;

import com.igodating.questionary.model.UserQuestionary;

import java.util.List;

public interface UserQuestionaryService {

    void createDraft(UserQuestionary userQuestionary);

    void update(UserQuestionary userQuestionary);

    void setStatusToPublished(UserQuestionary userQuestionary);

    void moveFromDraft(UserQuestionary userQuestionary);

    void delete(UserQuestionary userQuestionary);

    void updateEmbeddingAndSetProcessed(UserQuestionary userQuestionary);

    List<UserQuestionary> findUnprocessedWithLimit(int limit);
}
