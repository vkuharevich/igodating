package com.igodating.questionary.mapper;

import com.igodating.questionary.constant.SimilarityCalculatingOperator;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryCreateRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryDeleteRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryMoveFromDraftRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryRecommendation;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryUpdateRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryView;
import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.model.view.UserQuestionaryRecommendationView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { MatchingRuleMapper.class, QuestionBlockMapper.class, UserQuestionaryAnswerMapper.class })
public interface UserQuestionaryMapper {

    UserQuestionary createRequestToModel(UserQuestionaryCreateRequest userQuestionary);

    UserQuestionary updateRequestToModel(UserQuestionaryUpdateRequest userQuestionary);

    UserQuestionary deleteRequestToModel(UserQuestionaryDeleteRequest userQuestionary);

    UserQuestionary moveFromDraftRequestToModel(UserQuestionaryMoveFromDraftRequest userQuestionary);

    UserQuestionaryView modelToView(UserQuestionary userQuestionary);

    default UserQuestionaryRecommendation recommendationViewToDto(UserQuestionaryRecommendationView recommendationView,
                                                                  SimilarityCalculatingOperator similarityCalculatingOperator) {
        Double similarity = recommendationView.similarity();
        return new UserQuestionaryRecommendation(recommendationView.id(), recommendationView.userId(), similarity, getSimilarityPercentageBySimilarityCalculatingOperator(similarity, similarityCalculatingOperator));
    }

    default Integer getSimilarityPercentageBySimilarityCalculatingOperator(Double similarity, SimilarityCalculatingOperator similarityCalculatingOperator) {
        switch (similarityCalculatingOperator) {
            case EUCLID -> {
                double score = 1 - similarity;
                return score > 0 ? (int) (score * 100) : 0;
            }
            case COSINE -> {
                return (int) (((Math.PI - Math.acos(similarity)) * 100) / Math.PI);
            }
            case SCALAR -> {
                return (int) (similarity * -1 * 100);
            }
        }
        return 100;
    }
}
