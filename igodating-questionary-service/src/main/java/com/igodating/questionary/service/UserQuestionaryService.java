package com.igodating.questionary.service;

import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.model.UserQuestionaryAnswer;
import org.springframework.data.util.Pair;

import java.util.List;

public interface UserQuestionaryService {

    UserQuestionary getById(Long id);

    List<UserQuestionaryAnswer> getAllAnswersMatchedWithPublicRulesByTemplateIdAndUserId(Long templateId, String userId);

    void createDraft(UserQuestionary userQuestionary, String userId);

    void update(UserQuestionary userQuestionary, String userId);

    void setStatusToPublished(UserQuestionary userQuestionary);

    void moveFromDraft(UserQuestionary userQuestionary, String userId);

    void delete(UserQuestionary userQuestionary, String userId);

    void updateEmbeddingAndSetProcessed(UserQuestionary userQuestionary);

    List<UserQuestionary> findUnprocessedWithLimit(int limit);
}
