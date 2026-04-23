package com.igodating.mapper;

import com.igodating.dto.questionary.view.QuestionaryFullDto;
import com.igodating.dto.questionary.view.QuestionaryShortDto;
import com.igodating.model.Questionary;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { QuestionaryTypeMapper.class })
public interface QuestionaryMapper {

    QuestionaryFullDto modelToFullDto(Questionary questionary);

    QuestionaryShortDto modelToShortDto(Questionary questionary);
}
