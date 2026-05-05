package com.igodating.service.validation.impl;

import com.igodating.dto.questionarytype.create.AnswerOptionCreateDto;
import com.igodating.dto.questionarytype.create.QuestionBlockCreateDto;
import com.igodating.dto.questionarytype.create.QuestionCreateDto;
import com.igodating.dto.questionarytype.create.QuestionaryFilterCreateDto;
import com.igodating.dto.questionarytype.create.QuestionaryTypeCreateDto;
import com.igodating.exception.ValidationException;
import com.igodating.model.FilterType;
import com.igodating.model.QuestionType;
import com.igodating.service.validation.QuestionaryTypeValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.igodating.constant.Constants.*;

@Service
@RequiredArgsConstructor
public class QuestionaryTypeValidationServiceImpl implements QuestionaryTypeValidationService {

    @Override
    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    public void validateOnCreate(QuestionaryTypeCreateDto dto) {
        if (dto.name() == null) {
            throw new ValidationException("Название типа не заполнено");
        }
        if (dto.description() == null) {
            throw new ValidationException("Описание типа не заполнено");
        }
        if (dto.factorLambdaPart() == null) {
            throw new ValidationException("Поле factorLambdaPart не заполнено");
        }
        if (dto.factorLambdaPart() < LAMBDA_MIN) {
            throw new ValidationException("Поле factorLambdaPart не может быть меньше %s".formatted(LAMBDA_MIN));
        }
        if (dto.factorLambdaPart() > LAMBDA_MAX) {
            throw new ValidationException("Поле factorLambdaPart не может быть больше %s".formatted(LAMBDA_MAX));
        }
        if (CollectionUtils.isEmpty(dto.questionBlocks())) {
            throw new ValidationException("Блоки вопросов отсутствуют");
        }
        dto.questionBlocks().forEach(this::validateQuestionBlock);
    }

    private void validateQuestionBlock(QuestionBlockCreateDto questionBlockCreateDto) {
        if (questionBlockCreateDto.name() == null) {
            throw new ValidationException("Название блока не заполнено");
        }
        if (questionBlockCreateDto.order() == null) {
            throw new ValidationException("Порядок для блока не определен");
        }
        if (questionBlockCreateDto.description() == null) {
            throw new ValidationException("Описание блока не заполнено");
        }
        if (CollectionUtils.isEmpty(questionBlockCreateDto.questions())) {
            throw new ValidationException("Блок вопросов пуст");
        }
        questionBlockCreateDto.questions().forEach(this::validateQuestion);
    }

    private void validateQuestion(QuestionCreateDto questionCreateDto) {
        if (questionCreateDto.type() == null) {
            throw new ValidationException("Тип вопроса не заполнен");
        }
        if (questionCreateDto.title() == null) {
            throw new ValidationException("title вопроса не заполнен");
        }
        if ((questionCreateDto.type() == QuestionType.FREE_FORM || questionCreateDto.type() == QuestionType.CHOICE || questionCreateDto.type() == QuestionType.MULTIPLE_CHOICE) && questionCreateDto.weightCoefficientForVector() == null) {
            throw new ValidationException("Вес вопроса не заполнен");
        }
        if (questionCreateDto.weightCoefficientForVector() != null) {
            if (questionCreateDto.weightCoefficientForVector() < WEIGHT_MIN) {
                throw new ValidationException("Вес вопроса не может быть меньше %s".formatted(WEIGHT_MIN));
            }
            if (questionCreateDto.weightCoefficientForVector() > WEIGHT_MAX) {
                throw new ValidationException("Вес вопроса не может быть больше %s".formatted(WEIGHT_MAX));
            }
        }
        if (questionCreateDto.order() == null) {
            throw new ValidationException("Порядок для вопроса не определен");
        }
        if ((questionCreateDto.type() == QuestionType.CHOICE ||
                questionCreateDto.type() == QuestionType.MULTIPLE_CHOICE) && CollectionUtils.isEmpty(questionCreateDto.answerOptions())) {
            throw new ValidationException("Не определены варианты ответа");
        }
        if (questionCreateDto.type() != QuestionType.FREE_FORM && Boolean.TRUE.equals(questionCreateDto.forSemanticSimilarity())) {
            throw new ValidationException("Невозможно поставить флаг forSemanticSimilarity для вопроса не в свободной форме");
        }
        if (questionCreateDto.filter() != null) {
            validateFilter(questionCreateDto.filter(), questionCreateDto);
        }
        if (!CollectionUtils.isEmpty(questionCreateDto.answerOptions())) {
            questionCreateDto.answerOptions().forEach(this::validateOption);
        }
    }

    private void validateFilter(QuestionaryFilterCreateDto filterCreateDto, QuestionCreateDto questionCreateDto) {
        Set<String> answerOptionsKeys = questionCreateDto.answerOptions() == null ? new HashSet<>() : questionCreateDto.answerOptions().stream().map(AnswerOptionCreateDto::key).collect(Collectors.toSet());
        if (filterCreateDto.type() == null) {
            throw new ValidationException("Не определен тип фильтра");
        }
        if (filterCreateDto.type() == FilterType.CHOICE_IN_SET) {
            if (!Set.of(QuestionType.CHOICE, QuestionType.MULTIPLE_CHOICE).contains(questionCreateDto.type())) {
                throw new ValidationException("Тип вопроса не соответствует типу фильтра (с выбором)");
            }
            if (filterCreateDto.defaultNumericValueFrom() != null || filterCreateDto.defaultNumericValueTo() != null) {
                throw new ValidationException("Нельзя задать дефолтные числовые значения для фильтра с выбором");
            }
        }
        if (filterCreateDto.type() == FilterType.VALUE_IN_RANGE) {
            if (questionCreateDto.type() != QuestionType.NUMBER) {
                throw new ValidationException("Тип вопроса не соответствует типу фильтра (диапазон)");
            }
            if (filterCreateDto.defaultOptionsInSetKeys() != null) {
                throw new ValidationException("Нельзя задать дефолтные значения для выбора для числового фильра");
            }
        }
        if (!Boolean.TRUE.equals(filterCreateDto.isPublic())) {
            if (filterCreateDto.type() == FilterType.CHOICE_IN_SET
                    && (filterCreateDto.defaultOptionsInSetKeys() == null
                    || filterCreateDto.defaultOptionsInSetKeys().length == 0))  {
                throw new ValidationException("Не заданы дефолтные значения для choice-фильтра при том, что он скрытый");
            }
            if (filterCreateDto.type() == FilterType.VALUE_IN_RANGE
                    && filterCreateDto.defaultNumericValueTo() == null
                    && filterCreateDto.defaultNumericValueFrom() == null) {
                throw new ValidationException("Не заданы дефолтные числовые значения для числового фильтра при том, что он скрытый");
            }
        }
        if (filterCreateDto.defaultOptionsInSetKeys() != null) {
            for (String key : filterCreateDto.defaultOptionsInSetKeys()) {
                if (!answerOptionsKeys.contains(key)) {
                    throw new ValidationException("Неизвестный ключ опции %s в фильтре".formatted(key));
                }
            }
        }
    }

    private void validateOption(AnswerOptionCreateDto answerOption) {
        if (answerOption.value() == null) {
            throw new ValidationException("Значение для варианта ответа не заполнено");
        }
        if (answerOption.order() == null) {
            throw new ValidationException("Порядок варианта ответа не определен");
        }
        if (answerOption.influence() == null) {
            throw new ValidationException("Влияние варианта ответа не задано");
        }
    }
}
