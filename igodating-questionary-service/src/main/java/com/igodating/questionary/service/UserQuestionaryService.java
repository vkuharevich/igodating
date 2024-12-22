package com.igodating.questionary.service;

import com.igodating.questionary.constant.SimilarityCalculatingOperator;
import com.igodating.questionary.dto.filter.UserQuestionaryRecommendationRequest;
import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.model.UserQuestionaryAnswer;
import com.igodating.questionary.model.view.UserQuestionaryRecommendationView;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface UserQuestionaryService {

    <T> T getById(Long id, Function<UserQuestionary, T> mappingFunc);

    <T> List<T> getAllAnswersMatchedWithPublicRulesByTemplateIdAndUserId(Long templateId, String userId, Function<UserQuestionaryAnswer, T> mappingFunc);

    <T> Slice<T> findRecommendations(UserQuestionaryRecommendationRequest filter, String userId, BiFunction<UserQuestionaryRecommendationView, SimilarityCalculatingOperator, T> mappingFunc);

    void createDraft(UserQuestionary userQuestionary, String userId);

    void update(UserQuestionary userQuestionary, String userId);

    void setStatusToPublished(UserQuestionary userQuestionary);

    void moveFromDraft(UserQuestionary userQuestionary, String userId);

    void delete(UserQuestionary userQuestionary, String userId);

    void updateEmbeddingAndSetProcessed(UserQuestionary userQuestionary);

    List<UserQuestionary> findUnprocessedWithLimit(int limit);
}
