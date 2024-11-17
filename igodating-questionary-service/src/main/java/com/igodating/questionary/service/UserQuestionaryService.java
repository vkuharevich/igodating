package com.igodating.questionary.service;

import com.igodating.questionary.model.UserQuestionary;

import java.util.List;

public interface UserQuestionaryService {

    void createDraft(UserQuestionary userQuestionary, String userId);

    void update(UserQuestionary userQuestionary, String userId);

    void setStatusToPublished(UserQuestionary userQuestionary);

    void moveFromDraft(UserQuestionary userQuestionary, String userId);

    void delete(UserQuestionary userQuestionary, String userId);

    void updateEmbeddingAndSetProcessed(UserQuestionary userQuestionary);

    List<UserQuestionary> findUnprocessedWithLimit(int limit);
}
