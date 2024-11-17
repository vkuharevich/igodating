package com.igodating.questionary.mapper;

import com.igodating.questionary.dto.template.AnswerOptionCreateDto;
import com.igodating.questionary.dto.template.AnswerOptionUpdateDto;
import com.igodating.questionary.dto.template.MatchingRuleCreateDto;
import com.igodating.questionary.dto.template.QuestionCreateDto;
import com.igodating.questionary.dto.template.QuestionUpdateDto;
import com.igodating.questionary.model.AnswerOption;
import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.Question;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-17T13:23:49+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21 (Oracle Corporation)"
)
@Component
public class QuestionMapperImpl implements QuestionMapper {

    @Override
    public Question createDtoToModel(QuestionCreateDto question) {
        if ( question == null ) {
            return null;
        }

        Question question1 = new Question();

        question1.setMatchingRule( matchingRuleCreateDtoToMatchingRule( question.matchingRule() ) );
        question1.setTitle( question.title() );
        question1.setDescription( question.description() );
        question1.setAnswerType( question.answerType() );
        question1.setIsMandatory( question.isMandatory() );
        question1.setFromVal( question.fromVal() );
        question1.setToVal( question.toVal() );
        question1.setAnswerOptions( answerOptionCreateDtoListToAnswerOptionList( question.answerOptions() ) );

        return question1;
    }

    @Override
    public Question updateDtoToModel(QuestionUpdateDto question) {
        if ( question == null ) {
            return null;
        }

        Question question1 = new Question();

        question1.setId( question.id() );
        question1.setTitle( question.title() );
        question1.setDescription( question.description() );
        question1.setAnswerOptions( answerOptionUpdateDtoListToAnswerOptionList( question.answerOptions() ) );

        return question1;
    }

    protected MatchingRule matchingRuleCreateDtoToMatchingRule(MatchingRuleCreateDto matchingRuleCreateDto) {
        if ( matchingRuleCreateDto == null ) {
            return null;
        }

        MatchingRule matchingRule = new MatchingRule();

        matchingRule.setMatchingType( matchingRuleCreateDto.matchingType() );
        matchingRule.setPresetValue( matchingRuleCreateDto.presetValue() );
        matchingRule.setAccessType( matchingRuleCreateDto.accessType() );

        return matchingRule;
    }

    protected AnswerOption answerOptionCreateDtoToAnswerOption(AnswerOptionCreateDto answerOptionCreateDto) {
        if ( answerOptionCreateDto == null ) {
            return null;
        }

        AnswerOption answerOption = new AnswerOption();

        answerOption.setValue( answerOptionCreateDto.value() );

        return answerOption;
    }

    protected List<AnswerOption> answerOptionCreateDtoListToAnswerOptionList(List<AnswerOptionCreateDto> list) {
        if ( list == null ) {
            return null;
        }

        List<AnswerOption> list1 = new ArrayList<AnswerOption>( list.size() );
        for ( AnswerOptionCreateDto answerOptionCreateDto : list ) {
            list1.add( answerOptionCreateDtoToAnswerOption( answerOptionCreateDto ) );
        }

        return list1;
    }

    protected AnswerOption answerOptionUpdateDtoToAnswerOption(AnswerOptionUpdateDto answerOptionUpdateDto) {
        if ( answerOptionUpdateDto == null ) {
            return null;
        }

        AnswerOption answerOption = new AnswerOption();

        answerOption.setId( answerOptionUpdateDto.id() );
        answerOption.setValue( answerOptionUpdateDto.value() );

        return answerOption;
    }

    protected List<AnswerOption> answerOptionUpdateDtoListToAnswerOptionList(List<AnswerOptionUpdateDto> list) {
        if ( list == null ) {
            return null;
        }

        List<AnswerOption> list1 = new ArrayList<AnswerOption>( list.size() );
        for ( AnswerOptionUpdateDto answerOptionUpdateDto : list ) {
            list1.add( answerOptionUpdateDtoToAnswerOption( answerOptionUpdateDto ) );
        }

        return list1;
    }
}
