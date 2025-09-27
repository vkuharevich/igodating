package com.igodating.questionary.mapper;

import com.igodating.questionary.dto.template.MatchingRuleDefaultValuesCaseDto;
import com.igodating.questionary.dto.template.MatchingRuleDefaultValuesDto;
import com.igodating.questionary.dto.template.MatchingRuleView;
import com.igodating.questionary.dto.template.QuestionBlockView;
import com.igodating.questionary.dto.template.QuestionView;
import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.MatchingRuleDefaultValues;
import com.igodating.questionary.model.MatchingRuleDefaultValuesCase;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.QuestionBlock;
import com.igodating.questionary.model.constant.QuestionAnswerType;
import com.igodating.questionary.model.constant.RuleAccessType;
import com.igodating.questionary.model.constant.RuleMatchingType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-27T14:39:47+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21 (BellSoft)"
)
@Component
public class QuestionMapperImpl implements QuestionMapper {

    @Override
    public QuestionView modelToView(Question question) {
        if ( question == null ) {
            return null;
        }

        Long id = null;
        Long questionaryTemplateId = null;
        MatchingRuleView matchingRule = null;
        Long questionBlockId = null;
        QuestionBlockView questionBlock = null;
        String title = null;
        String description = null;
        QuestionAnswerType answerType = null;
        Boolean isMandatory = null;
        BigDecimal fromVal = null;
        BigDecimal toVal = null;
        LocalDateTime createdAt = null;
        List<String> answerOptions = null;

        id = question.getId();
        questionaryTemplateId = question.getQuestionaryTemplateId();
        matchingRule = matchingRuleToMatchingRuleView( question.getMatchingRule() );
        questionBlockId = question.getQuestionBlockId();
        questionBlock = questionBlockToQuestionBlockView( question.getQuestionBlock() );
        title = question.getTitle();
        description = question.getDescription();
        answerType = question.getAnswerType();
        isMandatory = question.getIsMandatory();
        fromVal = question.getFromVal();
        toVal = question.getToVal();
        createdAt = question.getCreatedAt();
        answerOptions = stringArrayToStringList( question.getAnswerOptions() );

        QuestionView questionView = new QuestionView( id, questionaryTemplateId, matchingRule, questionBlockId, questionBlock, title, description, answerType, isMandatory, fromVal, toVal, createdAt, answerOptions );

        return questionView;
    }

    protected MatchingRuleDefaultValuesCaseDto matchingRuleDefaultValuesCaseToMatchingRuleDefaultValuesCaseDto(MatchingRuleDefaultValuesCase matchingRuleDefaultValuesCase) {
        if ( matchingRuleDefaultValuesCase == null ) {
            return null;
        }

        String when = null;
        String then = null;

        when = matchingRuleDefaultValuesCase.getWhen();
        then = matchingRuleDefaultValuesCase.getThen();

        MatchingRuleDefaultValuesCaseDto matchingRuleDefaultValuesCaseDto = new MatchingRuleDefaultValuesCaseDto( when, then );

        return matchingRuleDefaultValuesCaseDto;
    }

    protected List<MatchingRuleDefaultValuesCaseDto> matchingRuleDefaultValuesCaseListToMatchingRuleDefaultValuesCaseDtoList(List<MatchingRuleDefaultValuesCase> list) {
        if ( list == null ) {
            return null;
        }

        List<MatchingRuleDefaultValuesCaseDto> list1 = new ArrayList<MatchingRuleDefaultValuesCaseDto>( list.size() );
        for ( MatchingRuleDefaultValuesCase matchingRuleDefaultValuesCase : list ) {
            list1.add( matchingRuleDefaultValuesCaseToMatchingRuleDefaultValuesCaseDto( matchingRuleDefaultValuesCase ) );
        }

        return list1;
    }

    protected MatchingRuleDefaultValuesDto matchingRuleDefaultValuesToMatchingRuleDefaultValuesDto(MatchingRuleDefaultValues matchingRuleDefaultValues) {
        if ( matchingRuleDefaultValues == null ) {
            return null;
        }

        List<MatchingRuleDefaultValuesCaseDto> cases = null;
        String defaultValue = null;

        cases = matchingRuleDefaultValuesCaseListToMatchingRuleDefaultValuesCaseDtoList( matchingRuleDefaultValues.getCases() );
        defaultValue = matchingRuleDefaultValues.getDefaultValue();

        MatchingRuleDefaultValuesDto matchingRuleDefaultValuesDto = new MatchingRuleDefaultValuesDto( cases, defaultValue );

        return matchingRuleDefaultValuesDto;
    }

    protected MatchingRuleView matchingRuleToMatchingRuleView(MatchingRule matchingRule) {
        if ( matchingRule == null ) {
            return null;
        }

        Long id = null;
        Long questionId = null;
        RuleMatchingType matchingType = null;
        MatchingRuleDefaultValuesDto defaultValues = null;
        RuleAccessType accessType = null;
        Boolean isMandatoryForMatching = null;
        LocalDateTime createdAt = null;

        id = matchingRule.getId();
        questionId = matchingRule.getQuestionId();
        matchingType = matchingRule.getMatchingType();
        defaultValues = matchingRuleDefaultValuesToMatchingRuleDefaultValuesDto( matchingRule.getDefaultValues() );
        accessType = matchingRule.getAccessType();
        isMandatoryForMatching = matchingRule.getIsMandatoryForMatching();
        createdAt = matchingRule.getCreatedAt();

        MatchingRuleView matchingRuleView = new MatchingRuleView( id, questionId, matchingType, defaultValues, accessType, isMandatoryForMatching, createdAt );

        return matchingRuleView;
    }

    protected QuestionBlockView questionBlockToQuestionBlockView(QuestionBlock questionBlock) {
        if ( questionBlock == null ) {
            return null;
        }

        Long id = null;
        String name = null;

        id = questionBlock.getId();
        name = questionBlock.getName();

        QuestionBlockView questionBlockView = new QuestionBlockView( id, name );

        return questionBlockView;
    }

    protected List<String> stringArrayToStringList(String[] stringArray) {
        if ( stringArray == null ) {
            return null;
        }

        List<String> list = new ArrayList<String>( stringArray.length );
        for ( String string : stringArray ) {
            list.add( string );
        }

        return list;
    }
}
