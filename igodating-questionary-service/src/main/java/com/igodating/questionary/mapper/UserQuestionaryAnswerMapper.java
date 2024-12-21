package com.igodating.questionary.mapper;

import com.igodating.questionary.dto.template.PublicFilterDescriptorDto;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryAnswerCreateDto;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryAnswerUpdateDto;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryAnswerView;
import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.QuestionBlock;
import com.igodating.questionary.model.UserQuestionaryAnswer;
import com.igodating.questionary.model.constant.QuestionAnswerType;
import com.igodating.questionary.model.constant.RuleAccessType;
import com.igodating.questionary.model.constant.RuleMatchingType;
import com.igodating.questionary.util.val.DefaultValueExtractor;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@Mapper(componentModel = "spring")
public abstract class UserQuestionaryAnswerMapper {

    @Autowired
    private DefaultValueExtractor defaultValueExtractor;

    public abstract UserQuestionaryAnswer createRequestToModel(UserQuestionaryAnswerCreateDto answer);

    public abstract UserQuestionaryAnswer updateRequestToModel(UserQuestionaryAnswerUpdateDto answer);

    public abstract UserQuestionaryAnswerView modelToView(UserQuestionaryAnswer answer);

    public PublicFilterDescriptorDto modelToPublicDescriptorDto(UserQuestionaryAnswer answer) {
        Question question = answer.getQuestion();
        MatchingRule matchingRule = question.getMatchingRule();
        QuestionBlock questionBlock = question.getQuestionBlock();

        assert Objects.equals(matchingRule.getAccessType(), RuleAccessType.PUBLIC);

        Long ruleId = matchingRule.getId();
        Long questionId = answer.getQuestionId();
        RuleMatchingType matchingType = matchingRule.getMatchingType();
        String defaultValue = defaultValueExtractor.extractDefaultValueForMatchingByAnswer(answer.getValue(), question);
        QuestionAnswerType answerType = question.getAnswerType();
        String questionBlockName = questionBlock == null ? null : questionBlock.getName();

        return new PublicFilterDescriptorDto(ruleId, questionId, matchingType, defaultValue, answerType, questionBlockName);
    }
}
