package com.igodating.questionary.service.validation.impl;

import com.igodating.commons.exception.ValidationException;
import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.QuestionBlock;
import com.igodating.questionary.model.QuestionaryTemplate;
import com.igodating.questionary.model.constant.QuestionAnswerType;
import com.igodating.questionary.repository.QuestionBlockRepository;
import com.igodating.questionary.repository.QuestionRepository;
import com.igodating.questionary.service.validation.MatchingRuleValidationService;
import com.igodating.questionary.service.validation.QuestionBlockValidationService;
import com.igodating.questionary.service.validation.QuestionValidationService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class QuestionValidationServiceImpl implements QuestionValidationService {

    private final QuestionRepository questionRepository;

    private final MatchingRuleValidationService matchingRuleValidationService;

    private final QuestionBlockRepository questionBlockRepository;

    private final QuestionBlockValidationService questionBlockValidationService;

    @Override
    @Transactional(readOnly = true)
    public void validateOnCreate(Question question) {
        if (question.getId() != null) {
            throw new ValidationException("Cannot create question with preset id");
        }

        checkCommonRequiredFieldsForCreateAndUpdateInQuestion(question);

        if (question.getQuestionBlockId() != null) {
            throw new ValidationException("Cannot provide block id on create");
        }

        if (question.getQuestionBlock() != null) {
            questionBlockValidationService.validateOnCreate(question.getQuestionBlock());
        }

        if (question.getMatchingRule() != null) {
            matchingRuleValidationService.validateOnCreate(question.getMatchingRule(), question);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnUpdateWithQuestionaryTemplate(Question question, QuestionaryTemplate questionaryTemplate) {
        checkCommonRequiredFieldsForCreateAndUpdateInQuestion(question);

        checkQuestionOnExistenceAndThrowIfDeleted(question);

        Long questionBlockId = question.getQuestionBlockId();
        if (questionBlockId != null) {
            QuestionBlock questionBlock = questionBlockRepository.findById(questionBlockId).orElseThrow(() -> new ValidationException(String.format("Block does'nt exist by such id %d", questionBlockId)));
            if (!Objects.equals(questionBlock.getQuestionaryTemplateId(), questionaryTemplate.getId())) {
                throw new ValidationException(String.format("Attempt to create a relation to wrong block (which related to template with id %d)", questionBlock.getQuestionaryTemplateId()));
            }
        }

        if (question.getQuestionBlock() != null) {
            questionBlockValidationService.validateOnCreate(question.getQuestionBlock());
        }

        MatchingRule matchingRule = question.getMatchingRule();
        if (matchingRule != null) {
            if (matchingRule.getId() == null) {
                matchingRuleValidationService.validateOnCreate(matchingRule, question);
            } else {
                matchingRuleValidationService.validateOnUpdate(matchingRule, question);
            }
        }
    }

    private void checkCommonRequiredFieldsForCreateAndUpdateInQuestion(Question question) {
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

        boolean withChoice = question.withChoice();

        if (!withChoice && !ArrayUtils.isEmpty(question.getAnswerOptions())) {
            throw new ValidationException("Answer options are provided for not-choice type");
        }

        if (withChoice) {
            if (ArrayUtils.isEmpty(question.getAnswerOptions()) || question.getAnswerOptions().length < 2) {
                throw new ValidationException("Answer options (minimum 2) are not provided for choice type");
            }
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
