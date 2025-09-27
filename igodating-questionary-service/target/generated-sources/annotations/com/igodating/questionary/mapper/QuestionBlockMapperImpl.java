package com.igodating.questionary.mapper;

import com.igodating.questionary.dto.template.QuestionBlockCreateDto;
import com.igodating.questionary.dto.template.QuestionBlockUpdateDto;
import com.igodating.questionary.dto.template.QuestionBlockView;
import com.igodating.questionary.model.QuestionBlock;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-27T14:39:47+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21 (BellSoft)"
)
@Component
public class QuestionBlockMapperImpl implements QuestionBlockMapper {

    @Override
    public QuestionBlock createRequestToModel(QuestionBlockCreateDto questionBlock) {
        if ( questionBlock == null ) {
            return null;
        }

        QuestionBlock questionBlock1 = new QuestionBlock();

        questionBlock1.setName( questionBlock.name() );
        questionBlock1.setDescription( questionBlock.description() );

        return questionBlock1;
    }

    @Override
    public QuestionBlock updateRequestToModel(QuestionBlockUpdateDto questionBlock) {
        if ( questionBlock == null ) {
            return null;
        }

        QuestionBlock questionBlock1 = new QuestionBlock();

        questionBlock1.setId( questionBlock.id() );
        questionBlock1.setName( questionBlock.name() );
        questionBlock1.setDescription( questionBlock.description() );

        return questionBlock1;
    }

    @Override
    public QuestionBlockView modelToView(QuestionBlock questionBlock) {
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
}
