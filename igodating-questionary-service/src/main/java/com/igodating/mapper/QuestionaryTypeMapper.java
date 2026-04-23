package com.igodating.mapper;

import com.igodating.dto.questionarytype.create.QuestionaryTypeCreateDto;
import com.igodating.dto.questionarytype.view.QuestionaryTypeFullDto;
import com.igodating.dto.questionarytype.view.QuestionaryTypeShortDto;
import com.igodating.model.QuestionaryType;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;

@Mapper(componentModel = "spring")
public interface QuestionaryTypeMapper {

    QuestionaryType createDtoToModel(QuestionaryTypeCreateDto type);

    QuestionaryTypeFullDto modelToFullDto(QuestionaryType type);

    QuestionaryTypeShortDto modelToDto(QuestionaryType type);

    @AfterMapping
    default void setParentForBlock(
            @MappingTarget QuestionaryType type
    ) {
        if (type.getQuestionBlocks() != null) {
            type.getQuestionBlocks().forEach(b -> {
                b.setQuestionaryType(type);
                b.getQuestions().forEach(q -> {
                    q.setBlock(b);
                    if (!CollectionUtils.isEmpty(q.getAnswerOptions())) {
                        q.getAnswerOptions().forEach(a -> {
                            a.setQuestion(q);
                        });
                    }
                    if (q.getFilter() != null) {
                        q.getFilter().setQuestion(q);
                    }
                });
            });
        }
    }
}
