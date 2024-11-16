package com.igodating.questionary.service.validation.impl;

import com.igodating.questionary.exception.ValidationException;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.constant.QuestionAnswerType;
import com.igodating.questionary.repository.QuestionRepository;
import com.igodating.questionary.service.validation.AnswerOptionValidationService;
import com.igodating.questionary.service.validation.MatchingRuleValidationService;
import com.igodating.questionary.service.validation.QuestionValidationService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class QuestionValidationServiceImpl implements QuestionValidationService {

    private final QuestionRepository questionRepository;

    private final AnswerOptionValidationService answerOptionValidationService;

    private final MatchingRuleValidationService matchingRuleValidationService;

    @Override
    @Transactional(readOnly = true)
    public void validateOnCreate(Question question) {
        if (question.getId() != null) {
            throw new ValidationException("Cannot create question with preset id");
        }

        checkCommonRequiredFieldsForCreateAndUpdateInQuestion(question);

        if (!CollectionUtils.isEmpty(question.getAnswerOptions())) {
            question.getAnswerOptions().forEach(answerOptionValidationService::validateOnCreate);
        }

        if (question.getMatchingRule() != null) {
            matchingRuleValidationService.validateOnCreate(question.getMatchingRule(), question);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnUpdate(Question question) {
        checkCommonRequiredFieldsForCreateAndUpdateInQuestion(question);

        checkQuestionOnExistenceAndThrowIfDeleted(question);

        if (!CollectionUtils.isEmpty(question.getAnswerOptions())) {
            question.getAnswerOptions().forEach(answerOptionValidationService::validateOnUpdate);
        }

        if (question.getMatchingRule() != null) {
            matchingRuleValidationService.validateOnUpdate(question.getMatchingRule(), question);
        }
    }

    private void checkCommonRequiredFieldsForCreateAndUpdateInQuestion(Question question) {
        if (question.getQuestionaryTemplateId() == null) {
            throw new ValidationException("Template is empty for question");
        }

        if (StringUtils.isBlank(question.getTitle())) {
            throw new ValidationException("Title is empty for question");
        }

        if (StringUtils.isBlank(question.getDescription())) {
            throw new ValidationException("Description is empty for question");
        }

        if (question.getAnswerType() == null) {
            throw new ValidationException("Answer type is empty for question");
        }

        if (!QuestionAnswerType.NUMERIC.equals(question.getAnswerType()) && (question.getFromVal() != null || question.getToVal() != null)) {
            throw new ValidationException("Cannot set fromVal or toVal for not numeric values");
        }

        boolean withChoice = QuestionAnswerType.CHOICE.equals(question.getAnswerType()) || QuestionAnswerType.MULTIPLE_CHOICE.equals(question.getAnswerType());

        if (!withChoice && !CollectionUtils.isEmpty(question.getAnswerOptions())) {
            throw new ValidationException("Answer options are provided for not-choice type");
        }

        if (withChoice && CollectionUtils.isEmpty(question.getAnswerOptions())) {
            throw new ValidationException("Answer options are not provided for choice type");
        }
    }

    private void checkQuestionOnExistenceAndThrowIfDeleted(Question question) {
        Long id = question.getId();
        if (id == null) {
            throw new ValidationException("Id is required for question");
        }

        questionRepository.findById(id).orElseThrow(() -> new ValidationException(String.format("Question doesn't exist by id %d", id)));
    }
}
