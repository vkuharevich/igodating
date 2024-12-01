package com.igodating.questionary.mapper;

import com.igodating.questionary.dto.template.QuestionBlockCreateDto;
import com.igodating.questionary.dto.template.QuestionBlockUpdateDto;
import com.igodating.questionary.dto.template.QuestionBlockView;
import com.igodating.questionary.model.QuestionBlock;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionBlockMapper {

    QuestionBlock createRequestToModel(QuestionBlockCreateDto questionBlock);

    QuestionBlock updateRequestToModel(QuestionBlockUpdateDto questionBlock);

    QuestionBlockView modelToView(QuestionBlock questionBlock);
}
