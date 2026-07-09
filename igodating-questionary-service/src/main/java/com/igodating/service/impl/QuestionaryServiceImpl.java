package com.igodating.service.impl;

import com.igodating.dto.questionary.create.AnswerCreateUpdateDto;
import com.igodating.dto.questionary.create.AnswersCreateUpdateDto;
import com.igodating.dto.questionary.create.QuestionaryCreateDto;
import com.igodating.dto.questionary.view.QuestionaryFullDto;
import com.igodating.dto.questionary.view.QuestionaryShortDto;
import com.igodating.mapper.QuestionaryMapper;
import com.igodating.model.Answer;
import com.igodating.model.Question;
import com.igodating.model.Questionary;
import com.igodating.model.QuestionaryStatus;
import com.igodating.model.QuestionaryType;
import com.igodating.repository.AnswerRepository;
import com.igodating.repository.QuestionaryRepository;
import com.igodating.service.QuestionaryService;
import com.igodating.service.QuestionaryTypeService;
import com.igodating.service.validation.QuestionaryValidationService;
import com.igodating.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class QuestionaryServiceImpl implements QuestionaryService {

    private final AnswerRepository answerRepository;

    private final QuestionaryMapper questionaryMapper;

    private final QuestionaryValidationService questionaryValidationService;

    private final QuestionaryRepository questionaryRepository;

    private final QuestionaryTypeService questionaryTypeService;

    @Override
    @Transactional(readOnly = true)
    public QuestionaryFullDto getById(Long id) {
        Questionary questionary = getModelById(id);

        return questionaryMapper.modelToFullDto(questionary);
    }

    @Override
    @Transactional
    public QuestionaryShortDto moveFromDraft(Long id) {
        Questionary questionary = getModelById(id);
        questionaryValidationService.validateOnMovingOnDraft(questionary);

        questionary.setStatus(QuestionaryStatus.PUBLISHED);
        questionary = questionaryRepository.save(questionary);

        return questionaryMapper.modelToShortDto(questionary);
    }

    @Override
    @Transactional
    public QuestionaryShortDto createOrUpdateAnswers(AnswersCreateUpdateDto answersCreateUpdateDto) {
        Questionary questionary = getModelById(answersCreateUpdateDto.questionaryId());
        questionaryValidationService.validateAnswers(questionary, answersCreateUpdateDto);
        Map<Long, Answer> answersByQuestionsMap = questionary.getAnswers().stream().collect(Collectors.toMap(k -> k.getQuestion().getId(), v -> v));

        for (AnswerCreateUpdateDto answerCreateUpdateDto : answersCreateUpdateDto.answers()) {
            Answer answer = answersByQuestionsMap.get(answerCreateUpdateDto.questionId());
            if (answer == null) {
                answer = new Answer();
                answer.setQuestionary(questionary);
                Question questionMock = new Question();
                questionMock.setId(answerCreateUpdateDto.questionId());
                answer.setQuestion(questionMock);
            }
            answer.setChosenOptions(answerCreateUpdateDto.chosenOptions());
            answer.setTextValue(answerCreateUpdateDto.textValue());
            answer.setNumericValue(answerCreateUpdateDto.numericValue());
            answerRepository.save(answer);
        }

        return questionaryMapper.modelToShortDto(questionary);
    }

    @Override
    @Transactional
    public QuestionaryShortDto createDraft(QuestionaryCreateDto questionaryCreateDto) {
        questionaryValidationService.validateOnCreate(questionaryCreateDto);

        Questionary questionary = new Questionary();
        questionary.setType(questionaryTypeService.getTypeModel(questionaryCreateDto.getQuestionaryTypeId()));
        questionary.setName(questionaryCreateDto.getName());
        questionary.setStatus(QuestionaryStatus.DRAFT);
        questionary.setUserId(questionaryCreateDto.getUserId());

        questionary = questionaryRepository.save(questionary);

        return questionaryMapper.modelToShortDto(questionary);
    }

    @Override
    @Transactional(readOnly = true)
    public Questionary getModelById(Long id) {
        Questionary questionary = questionaryRepository.findById(id).orElseThrow();
        QuestionaryType type = questionaryTypeService.getTypeModel(questionary.getType().getId());
        questionary.setType(type);
        return questionary;
    }
}
