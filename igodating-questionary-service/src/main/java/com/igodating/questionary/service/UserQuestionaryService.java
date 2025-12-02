package com.igodating.questionary.service;

import com.igodating.questionary.dto.filter.UserQuestionaryRecommendationRequest;
import com.igodating.questionary.dto.template.PublicFilterDescriptorDto;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryCreateRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryDeleteRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryMoveFromDraftRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryRecommendation;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryUpdateRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryView;
import com.igodating.questionary.model.UserQuestionary;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface UserQuestionaryService {

    UserQuestionaryView getById(Long id);

    List<PublicFilterDescriptorDto> getAllAnswersMatchedWithPublicRulesByTemplateIdAndUserId(Long templateId, String userId);

    Slice<UserQuestionaryRecommendation> findRecommendations(UserQuestionaryRecommendationRequest filter, String userId);

    Long createDraft(UserQuestionaryCreateRequest userQuestionaryCreateRequest, String userId);

    Long update(UserQuestionaryUpdateRequest userQuestionaryUpdateRequest, String userId);

    void setStatusToPublished(UserQuestionary userQuestionary);

    Long moveFromDraft(UserQuestionaryMoveFromDraftRequest userQuestionaryMoveFromDraftRequest, String userId);

    Long delete(UserQuestionaryDeleteRequest userQuestionaryDeleteRequest, String userId);

    void updateEmbeddingAndSetProcessed(UserQuestionary userQuestionary);

    List<UserQuestionary> findUnprocessedWithLimit(int limit);
}
