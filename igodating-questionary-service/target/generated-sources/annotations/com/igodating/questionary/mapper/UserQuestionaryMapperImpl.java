package com.igodating.questionary.mapper;

import com.igodating.questionary.dto.userquestionary.UserQuestionaryAnswerCreateDto;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryAnswerUpdateDto;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryAnswerView;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryCreateRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryDeleteRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryMoveFromDraftRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryUpdateRequest;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryView;
import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.model.UserQuestionaryAnswer;
import com.igodating.questionary.model.constant.UserQuestionaryStatus;
import java.time.LocalDateTime;
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
public class UserQuestionaryMapperImpl implements UserQuestionaryMapper {

    @Autowired
    private UserQuestionaryAnswerMapper userQuestionaryAnswerMapper;

    @Override
    public UserQuestionary createRequestToModel(UserQuestionaryCreateRequest userQuestionary) {
        if ( userQuestionary == null ) {
            return null;
        }

        UserQuestionary userQuestionary1 = new UserQuestionary();

        userQuestionary1.setTitle( userQuestionary.title() );
        userQuestionary1.setDescription( userQuestionary.description() );
        userQuestionary1.setQuestionaryTemplateId( userQuestionary.questionaryTemplateId() );
        userQuestionary1.setAnswers( userQuestionaryAnswerCreateDtoListToUserQuestionaryAnswerList( userQuestionary.answers() ) );

        return userQuestionary1;
    }

    @Override
    public UserQuestionary updateRequestToModel(UserQuestionaryUpdateRequest userQuestionary) {
        if ( userQuestionary == null ) {
            return null;
        }

        UserQuestionary userQuestionary1 = new UserQuestionary();

        userQuestionary1.setId( userQuestionary.id() );
        userQuestionary1.setTitle( userQuestionary.title() );
        userQuestionary1.setDescription( userQuestionary.description() );
        userQuestionary1.setAnswers( userQuestionaryAnswerUpdateDtoListToUserQuestionaryAnswerList( userQuestionary.answers() ) );

        return userQuestionary1;
    }

    @Override
    public UserQuestionary deleteRequestToModel(UserQuestionaryDeleteRequest userQuestionary) {
        if ( userQuestionary == null ) {
            return null;
        }

        UserQuestionary userQuestionary1 = new UserQuestionary();

        userQuestionary1.setId( userQuestionary.id() );

        return userQuestionary1;
    }

    @Override
    public UserQuestionary moveFromDraftRequestToModel(UserQuestionaryMoveFromDraftRequest userQuestionary) {
        if ( userQuestionary == null ) {
            return null;
        }

        UserQuestionary userQuestionary1 = new UserQuestionary();

        userQuestionary1.setId( userQuestionary.id() );

        return userQuestionary1;
    }

    @Override
    public UserQuestionaryView modelToView(UserQuestionary userQuestionary) {
        if ( userQuestionary == null ) {
            return null;
        }

        Long id = null;
        String title = null;
        String description = null;
        String userId = null;
        Long questionaryTemplateId = null;
        UserQuestionaryStatus questionaryStatus = null;
        LocalDateTime createdAt = null;
        LocalDateTime deletedAt = null;
        List<UserQuestionaryAnswerView> answers = null;

        id = userQuestionary.getId();
        title = userQuestionary.getTitle();
        description = userQuestionary.getDescription();
        userId = userQuestionary.getUserId();
        questionaryTemplateId = userQuestionary.getQuestionaryTemplateId();
        questionaryStatus = userQuestionary.getQuestionaryStatus();
        createdAt = userQuestionary.getCreatedAt();
        deletedAt = userQuestionary.getDeletedAt();
        answers = userQuestionaryAnswerListToUserQuestionaryAnswerViewList( userQuestionary.getAnswers() );

        UserQuestionaryView userQuestionaryView = new UserQuestionaryView( id, title, description, userId, questionaryTemplateId, questionaryStatus, createdAt, deletedAt, answers );

        return userQuestionaryView;
    }

    protected List<UserQuestionaryAnswer> userQuestionaryAnswerCreateDtoListToUserQuestionaryAnswerList(List<UserQuestionaryAnswerCreateDto> list) {
        if ( list == null ) {
            return null;
        }

        List<UserQuestionaryAnswer> list1 = new ArrayList<UserQuestionaryAnswer>( list.size() );
        for ( UserQuestionaryAnswerCreateDto userQuestionaryAnswerCreateDto : list ) {
            list1.add( userQuestionaryAnswerMapper.createRequestToModel( userQuestionaryAnswerCreateDto ) );
        }

        return list1;
    }

    protected List<UserQuestionaryAnswer> userQuestionaryAnswerUpdateDtoListToUserQuestionaryAnswerList(List<UserQuestionaryAnswerUpdateDto> list) {
        if ( list == null ) {
            return null;
        }

        List<UserQuestionaryAnswer> list1 = new ArrayList<UserQuestionaryAnswer>( list.size() );
        for ( UserQuestionaryAnswerUpdateDto userQuestionaryAnswerUpdateDto : list ) {
            list1.add( userQuestionaryAnswerMapper.updateRequestToModel( userQuestionaryAnswerUpdateDto ) );
        }

        return list1;
    }

    protected List<UserQuestionaryAnswerView> userQuestionaryAnswerListToUserQuestionaryAnswerViewList(List<UserQuestionaryAnswer> list) {
        if ( list == null ) {
            return null;
        }

        List<UserQuestionaryAnswerView> list1 = new ArrayList<UserQuestionaryAnswerView>( list.size() );
        for ( UserQuestionaryAnswer userQuestionaryAnswer : list ) {
            list1.add( userQuestionaryAnswerMapper.modelToView( userQuestionaryAnswer ) );
        }

        return list1;
    }
}
