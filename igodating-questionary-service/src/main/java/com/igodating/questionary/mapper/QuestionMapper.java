package com.igodating.questionary.mapper;

import com.igodating.questionary.dto.template.QuestionView;
import com.igodating.questionary.model.Question;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    QuestionView modelToView(Question question);
}
