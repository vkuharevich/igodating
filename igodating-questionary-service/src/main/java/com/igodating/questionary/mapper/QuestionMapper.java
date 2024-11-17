package com.igodating.questionary.mapper;

import com.igodating.questionary.dto.template.AnswerOptionView;
import com.igodating.questionary.dto.template.MatchingRuleView;
import com.igodating.questionary.dto.template.QuestionCreateDto;
import com.igodating.questionary.dto.template.QuestionUpdateDto;
import com.igodating.questionary.dto.template.QuestionView;
import com.igodating.questionary.model.AnswerOption;
import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.Question;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    Question createDtoToModel(QuestionCreateDto question);

    Question updateDtoToModel(QuestionUpdateDto question);

    default QuestionView modelToView(Question question) {
        List<AnswerOptionView> answerOptions = null;

        if (question.withChoice()) {
            answerOptions = question.getAnswerOptions().stream().map(this::answerOptionToView).toList();
        }

        MatchingRuleView matchingRule = question.getMatchingRule() == null ? null : matchingRuleToView(question.getMatchingRule());

        return new QuestionView(question.getId(),
                question.getQuestionaryTemplateId(),
                matchingRule, question.getTitle(),
                question.getDescription(),
                question.getAnswerType(),
                question.getIsMandatory(),
                question.getFromVal(),
                question.getToVal(),
                question.getCreatedAt(),
                answerOptions);
    };

    default AnswerOptionView answerOptionToView(AnswerOption answerOption) {
        return new AnswerOptionView(answerOption.getId(), answerOption.getQuestionId(), answerOption.getValue());
    }

    default MatchingRuleView matchingRuleToView(MatchingRule matchingRule) {
        return new MatchingRuleView(matchingRule.getId(), matchingRule.getQuestionId(), matchingRule.getMatchingType(), matchingRule.getPresetValue(), matchingRule.getAccessType(), matchingRule.getCreatedAt());
    }
}
