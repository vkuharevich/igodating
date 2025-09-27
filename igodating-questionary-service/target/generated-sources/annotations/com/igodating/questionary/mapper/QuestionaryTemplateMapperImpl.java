package com.igodating.questionary.mapper;

import com.igodating.questionary.dto.template.QuestionCreateDto;
import com.igodating.questionary.dto.template.QuestionUpdateDto;
import com.igodating.questionary.dto.template.QuestionView;
import com.igodating.questionary.dto.template.QuestionaryTemplateCreateRequest;
import com.igodating.questionary.dto.template.QuestionaryTemplateDeleteRequest;
import com.igodating.questionary.dto.template.QuestionaryTemplateUpdateRequest;
import com.igodating.questionary.dto.template.QuestionaryTemplateView;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.QuestionaryTemplate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-27T14:39:47+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21 (BellSoft)"
)
@Component
public class QuestionaryTemplateMapperImpl implements QuestionaryTemplateMapper {

    @Autowired
    private MatchingRuleMapper matchingRuleMapper;
    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public QuestionaryTemplate createRequestToModel(QuestionaryTemplateCreateRequest questionaryTemplate) {
        if ( questionaryTemplate == null ) {
            return null;
        }

        QuestionaryTemplate questionaryTemplate1 = new QuestionaryTemplate();

        questionaryTemplate1.setName( questionaryTemplate.name() );
        questionaryTemplate1.setDescription( questionaryTemplate.description() );
        questionaryTemplate1.setQuestions( questionCreateDtoListToQuestionList( questionaryTemplate.questions() ) );

        return questionaryTemplate1;
    }

    @Override
    public QuestionaryTemplate updateRequestToModel(QuestionaryTemplateUpdateRequest questionaryTemplate) {
        if ( questionaryTemplate == null ) {
            return null;
        }

        QuestionaryTemplate questionaryTemplate1 = new QuestionaryTemplate();

        questionaryTemplate1.setId( questionaryTemplate.id() );
        questionaryTemplate1.setName( questionaryTemplate.name() );
        questionaryTemplate1.setDescription( questionaryTemplate.description() );
        questionaryTemplate1.setQuestions( questionUpdateDtoListToQuestionList( questionaryTemplate.questions() ) );

        return questionaryTemplate1;
    }

    @Override
    public QuestionaryTemplate deleteRequestToModel(QuestionaryTemplateDeleteRequest questionaryTemplate) {
        if ( questionaryTemplate == null ) {
            return null;
        }

        QuestionaryTemplate questionaryTemplate1 = new QuestionaryTemplate();

        questionaryTemplate1.setId( questionaryTemplate.id() );

        return questionaryTemplate1;
    }

    @Override
    public QuestionaryTemplateView modelToView(QuestionaryTemplate questionaryTemplate) {
        if ( questionaryTemplate == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        String description = null;
        List<QuestionView> questions = null;

        id = questionaryTemplate.getId();
        name = questionaryTemplate.getName();
        description = questionaryTemplate.getDescription();
        questions = questionListToQuestionViewList( questionaryTemplate.getQuestions() );

        QuestionaryTemplateView questionaryTemplateView = new QuestionaryTemplateView( id, name, description, questions );

        return questionaryTemplateView;
    }

    protected String[] stringListToStringArray(List<String> list) {
        if ( list == null ) {
            return null;
        }

        String[] stringTmp = new String[list.size()];
        int i = 0;
        for ( String string : list ) {
            stringTmp[i] = string;
            i++;
        }

        return stringTmp;
    }

    protected Question questionCreateDtoToQuestion(QuestionCreateDto questionCreateDto) {
        if ( questionCreateDto == null ) {
            return null;
        }

        Question question = new Question();

        question.setQuestionBlockId( questionCreateDto.questionBlockId() );
        question.setMatchingRule( matchingRuleMapper.createRequestToModel( questionCreateDto.matchingRule() ) );
        question.setTitle( questionCreateDto.title() );
        question.setDescription( questionCreateDto.description() );
        question.setAnswerType( questionCreateDto.answerType() );
        question.setIsMandatory( questionCreateDto.isMandatory() );
        question.setFromVal( questionCreateDto.fromVal() );
        question.setToVal( questionCreateDto.toVal() );
        question.setAnswerOptions( stringListToStringArray( questionCreateDto.answerOptions() ) );

        return question;
    }

    protected List<Question> questionCreateDtoListToQuestionList(List<QuestionCreateDto> list) {
        if ( list == null ) {
            return null;
        }

        List<Question> list1 = new ArrayList<Question>( list.size() );
        for ( QuestionCreateDto questionCreateDto : list ) {
            list1.add( questionCreateDtoToQuestion( questionCreateDto ) );
        }

        return list1;
    }

    protected Question questionUpdateDtoToQuestion(QuestionUpdateDto questionUpdateDto) {
        if ( questionUpdateDto == null ) {
            return null;
        }

        Question question = new Question();

        question.setId( questionUpdateDto.id() );
        question.setQuestionBlockId( questionUpdateDto.questionBlockId() );
        question.setMatchingRule( matchingRuleMapper.updateRequestToModel( questionUpdateDto.matchingRule() ) );
        question.setTitle( questionUpdateDto.title() );
        question.setDescription( questionUpdateDto.description() );
        question.setAnswerType( questionUpdateDto.answerType() );
        question.setIsMandatory( questionUpdateDto.isMandatory() );
        question.setFromVal( questionUpdateDto.fromVal() );
        question.setToVal( questionUpdateDto.toVal() );
        question.setAnswerOptions( stringListToStringArray( questionUpdateDto.answerOptions() ) );

        return question;
    }

    protected List<Question> questionUpdateDtoListToQuestionList(List<QuestionUpdateDto> list) {
        if ( list == null ) {
            return null;
        }

        List<Question> list1 = new ArrayList<Question>( list.size() );
        for ( QuestionUpdateDto questionUpdateDto : list ) {
            list1.add( questionUpdateDtoToQuestion( questionUpdateDto ) );
        }

        return list1;
    }

    protected List<QuestionView> questionListToQuestionViewList(List<Question> list) {
        if ( list == null ) {
            return null;
        }

        List<QuestionView> list1 = new ArrayList<QuestionView>( list.size() );
        for ( Question question : list ) {
            list1.add( questionMapper.modelToView( question ) );
        }

        return list1;
    }
}
