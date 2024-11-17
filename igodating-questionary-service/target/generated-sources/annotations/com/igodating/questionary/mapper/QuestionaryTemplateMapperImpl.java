package com.igodating.questionary.mapper;

import com.igodating.questionary.dto.template.QuestionCreateDto;
import com.igodating.questionary.dto.template.QuestionUpdateDto;
import com.igodating.questionary.dto.template.QuestionView;
import com.igodating.questionary.dto.template.QuestionaryTemplateCreateRequest;
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
    date = "2024-11-17T13:48:47+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21 (Oracle Corporation)"
)
@Component
public class QuestionaryTemplateMapperImpl implements QuestionaryTemplateMapper {

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

    protected List<Question> questionCreateDtoListToQuestionList(List<QuestionCreateDto> list) {
        if ( list == null ) {
            return null;
        }

        List<Question> list1 = new ArrayList<Question>( list.size() );
        for ( QuestionCreateDto questionCreateDto : list ) {
            list1.add( questionMapper.createDtoToModel( questionCreateDto ) );
        }

        return list1;
    }

    protected List<Question> questionUpdateDtoListToQuestionList(List<QuestionUpdateDto> list) {
        if ( list == null ) {
            return null;
        }

        List<Question> list1 = new ArrayList<Question>( list.size() );
        for ( QuestionUpdateDto questionUpdateDto : list ) {
            list1.add( questionMapper.updateDtoToModel( questionUpdateDto ) );
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
