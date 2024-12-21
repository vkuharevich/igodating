package com.igodating.questionary.mapper;

import com.igodating.questionary.dto.userquestionary.UserQuestionaryCreateRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryUpdateRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryView;
import com.igodating.questionary.model.UserQuestionary;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { MatchingRuleMapper.class, QuestionBlockMapper.class, UserQuestionaryAnswerMapper.class })
public interface UserQuestionaryMapper {

    UserQuestionary createRequestToModel(UserQuestionaryCreateRequest userQuestionary);

    UserQuestionary updateRequestToModel(UserQuestionaryUpdateRequest userQuestionary);

    UserQuestionaryView modelToView(UserQuestionary userQuestionary);
}
