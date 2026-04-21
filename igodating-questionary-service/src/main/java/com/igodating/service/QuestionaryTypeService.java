package com.igodating.service;

import com.igodating.dto.questionarytype.create.QuestionaryTypeCreateDto;
import com.igodating.dto.questionarytype.view.QuestionaryTypeFullDto;
import com.igodating.dto.questionarytype.view.QuestionaryTypeShortDto;
import com.igodating.model.QuestionaryType;

import java.util.List;

public interface QuestionaryTypeService {

    QuestionaryType getTypeModel(long id);

    QuestionaryTypeFullDto getType(long id);

    List<QuestionaryTypeShortDto> getAllTypes();

    QuestionaryTypeShortDto create(QuestionaryTypeCreateDto createDto);
}
