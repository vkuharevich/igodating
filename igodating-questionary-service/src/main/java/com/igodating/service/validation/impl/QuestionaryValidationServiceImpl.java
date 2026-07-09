package com.igodating.service.validation.impl;

import com.igodating.dto.questionary.create.AnswerCreateUpdateDto;
import com.igodating.dto.questionary.create.AnswersCreateUpdateDto;
import com.igodating.dto.questionary.create.QuestionaryCreateDto;
import com.igodating.exception.ValidationException;
import com.igodating.model.AnswerOption;
import com.igodating.model.Question;
import com.igodating.model.QuestionType;
import com.igodating.model.Questionary;
import com.igodating.model.QuestionaryStatus;
import com.igodating.model.QuestionaryType;
import com.igodating.service.validation.QuestionaryValidationService;
import com.igodating.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionaryValidationServiceImpl implements QuestionaryValidationService {

    @Override
    public void validateOnCreate(QuestionaryCreateDto questionaryCreateDto) {
        if (questionaryCreateDto.getName() == null) {
            throw new ValidationException("Название анкеты не заполнено");
        }
        if (questionaryCreateDto.getQuestionaryTypeId() == null) {
            throw new ValidationException("Тип анкеты не выбран");
        }
    }

    @Override
    public void validateOnMovingOnDraft(Questionary questionary) {
        if (questionary.getStatus() != QuestionaryStatus.DRAFT) {
            throw new ValidationException("Статус для перевода может быть только DRAFT");
        }
        long totalQuestions = questionary.getType().getQuestionBlocks().stream().mapToLong(b -> b.getQuestions().size()).sum();
        if (questionary.getAnswers().size() != totalQuestions) {
            throw new ValidationException("Не все ответы были даны");
        }
    }

    @Override
    public void validateAnswers(Questionary questionary, AnswersCreateUpdateDto answersCreateUpdateDto) {
        if (CollectionUtils.isEmpty(answersCreateUpdateDto.answers())) {
            throw new ValidationException("Не переданы изменившиеся ответы");
        }
        if (questionary.getStatus() != QuestionaryStatus.DRAFT) {
            throw new ValidationException("Менять можно только анкеты-черновики");
        }
//        if (!Objects.equals(questionary.getUserId(), UserUtils.getCurrentUserId())) {
//            throw new ValidationException("Юзер не имеет права на изменение анкеты %s".formatted(answersCreateUpdateDto.questionaryId()));
//        }
        QuestionaryType type = questionary.getType();
        Map<Long, Question> questions = type.getQuestionBlocks().stream().flatMap(b -> b.getQuestions().stream()).collect(Collectors.toMap(Question::getId, v -> v));
        for (AnswerCreateUpdateDto answerCreateUpdateDto : answersCreateUpdateDto.answers()) {
            Question question = questions.get(answerCreateUpdateDto.questionId());
            if (question == null) {
                throw new ValidationException("Вопроса с ID = %s не существует в анкете".formatted(answerCreateUpdateDto.questionId()));
            }
            boolean optionsAreChosen = answerCreateUpdateDto.chosenOptions() != null && answerCreateUpdateDto.chosenOptions().length > 0;
            if (question.getType() == QuestionType.FREE_FORM) {
                if (answerCreateUpdateDto.textValue() == null) {
                    throw new ValidationException("Текстовый вопрос не отвечен");
                }
                if (answerCreateUpdateDto.numericValue() != null) {
                    throw new ValidationException("Передано числовое значение для вопроса в вольной форме");
                }
                if (optionsAreChosen) {
                    throw new ValidationException("Переданы выбранные опции для вопроса в вольной форме");
                }
            }
            if (question.withChoice()) {
                if (answerCreateUpdateDto.chosenOptions() == null || answerCreateUpdateDto.chosenOptions().length == 0) {
                    throw new ValidationException("Ничего не выбрано из опций ответа");
                }
                if (question.getType() == QuestionType.CHOICE && answerCreateUpdateDto.chosenOptions().length > 1) {
                    throw new ValidationException("Недопустимо выбирать больше 1 опции");
                }
                Set<String> correspondedOptions = question.getAnswerOptions().stream().map(AnswerOption::getKey).collect(Collectors.toSet());
                for (String chosenOption : answerCreateUpdateDto.chosenOptions()) {
                    if (!correspondedOptions.contains(chosenOption)) {
                        throw new ValidationException("Выбрана опция с ключом %s, не представленная в вопросе".formatted(chosenOption));
                    }
                }
            }
            if (question.getType() == QuestionType.NUMBER) {
                if (answerCreateUpdateDto.numericValue() == null) {
                    throw new ValidationException("Не введено число для числового вопроса");
                }
                if (answerCreateUpdateDto.textValue() != null) {
                    throw new ValidationException("Передано текстовое значение для числового вопроса");
                }
                if (optionsAreChosen) {
                    throw new ValidationException("Переданы выбранные опции для вопроса в числовой форме");
                }
            }
        }
    }
}
