package com.igodating.service;

import com.igodating.dto.questionary.create.AnswersCreateUpdateDto;
import com.igodating.dto.questionary.create.QuestionaryCreateDto;
import com.igodating.dto.questionary.view.QuestionaryFullDto;
import com.igodating.dto.questionary.view.QuestionaryShortDto;
import com.igodating.model.Questionary;

public interface QuestionaryService {

    QuestionaryFullDto getById(Long id);

    QuestionaryShortDto moveFromDraft(Long id);

    QuestionaryShortDto createOrUpdateAnswers(AnswersCreateUpdateDto answersCreateUpdateDto);

    QuestionaryShortDto createDraft(QuestionaryCreateDto questionaryCreateDto);

    Questionary getModelById(Long id);
}
