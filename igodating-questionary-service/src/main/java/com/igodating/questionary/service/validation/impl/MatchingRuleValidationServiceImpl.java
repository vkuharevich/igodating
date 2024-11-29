package com.igodating.questionary.service.validation.impl;

import com.igodating.questionary.exception.ValidationException;
import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.constant.QuestionAnswerType;
import com.igodating.questionary.model.constant.RuleAccessType;
import com.igodating.questionary.model.constant.RuleMatchingType;
import com.igodating.questionary.repository.MatchingRuleRepository;
import com.igodating.questionary.service.validation.MatchingRuleValidationService;
import com.igodating.questionary.service.validation.AnswerValueFormatValidationService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchingRuleValidationServiceImpl implements MatchingRuleValidationService {

    private final MatchingRuleRepository matchingRuleRepository;

    private final AnswerValueFormatValidationService answerValueFormatValidationService;

    @Override
    @Transactional(readOnly = true)
    public void validateOnCreate(MatchingRule matchingRule, Question question) {
        if (matchingRule.getId() != null) {
            throw new ValidationException("Cannot create matching rule with preset id");
        }

        checkCommonRequiredFieldsForCreateAndUpdateInMatchingRule(matchingRule, question);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnUpdate(MatchingRule matchingRule, Question question) {
        checkCommonRequiredFieldsForCreateAndUpdateInMatchingRule(matchingRule, question);

        checkMatchingRuleOnExistenceAndThrowIfDeleted(matchingRule);
    }

    private void checkCommonRequiredFieldsForCreateAndUpdateInMatchingRule(MatchingRule matchingRule, Question question) {
        QuestionAnswerType answerType = question.getAnswerType();
        RuleMatchingType matchingType = matchingRule.getMatchingType();
        RuleAccessType accessType = matchingRule.getAccessType();

        if (matchingType == null) {
            throw new ValidationException("Matching type is empty for rule");
        }

        if (accessType == null) {
            throw new ValidationException("Access type is empty for rule");
        }

        if (RuleMatchingType.SEMANTIC_RANGING.equals(matchingType) && !RuleAccessType.PRIVATE.equals(accessType)) {
            throw new ValidationException("Semantic ranging cannot be public");
        }

        if (RuleMatchingType.SEMANTIC_RANGING.equals(matchingType) && StringUtils.isNotEmpty(matchingRule.getPresetValue())) {
            throw new ValidationException("Cannot provide preset value for Semantic Ranging");
        }

        if (RuleMatchingType.LIKE.equals(matchingType) && !answerType.equals(QuestionAnswerType.FREE_FORM)) {
            throw new ValidationException("Like can be applied only to free form");
        }

        if (RuleMatchingType.IN_RANGE.equals(matchingType) && !answerType.equals(QuestionAnswerType.NUMERIC)) {
            throw new ValidationException("In range can be applied only to numeric");
        }

        if (RuleMatchingType.IN_SET.equals(matchingType) && !(answerType.equals(QuestionAnswerType.CHOICE) || answerType.equals(QuestionAnswerType.MULTIPLE_CHOICE))) {
            throw new ValidationException("In set can be applied only to choice");
        }

        if (RuleMatchingType.EQUALS.equals(matchingType) && !(answerType.equals(QuestionAnswerType.FREE_FORM) || answerType.equals(QuestionAnswerType.NUMERIC) || answerType.equals(QuestionAnswerType.CHOICE))) {
            throw new ValidationException("Equals can be applied only to free form, numeric or one-choice");
        }

        if (RuleMatchingType.NOT_EQUALS.equals(matchingType) && !(answerType.equals(QuestionAnswerType.FREE_FORM) || answerType.equals(QuestionAnswerType.NUMERIC) || answerType.equals(QuestionAnswerType.CHOICE))) {
            throw new ValidationException("Not equals can be applied only to free form, numeric or one-choice");
        }

//        if (RuleAccessType.PRIVATE.equals(accessType) && !matchingType.equals(RuleMatchingType.SEMANTIC_RANGING) && StringUtils.isBlank(matchingRule.getPresetValue())) {
//            throw new ValidationException("Private access with non-semantic matching should have a preset value");
//        }

        if (RuleAccessType.PRIVATE.equals(accessType) && !Boolean.TRUE.equals(matchingRule.getIsMandatoryForMatching())) {
            throw new ValidationException("Private access should be mandatory for matching");
        }

        if (StringUtils.isNotEmpty(matchingRule.getPresetValue())) {
            answerValueFormatValidationService.validateValueWithQuestion(matchingRule.getPresetValue(), question);
        }
    }

    private void checkMatchingRuleOnExistenceAndThrowIfDeleted(MatchingRule matchingRule) {
        Long id = matchingRule.getId();
        if (id == null) {
            throw new ValidationException("Id is required for rule updating");
        }

        matchingRuleRepository.findById(id).orElseThrow(() -> new ValidationException(String.format("Matching rule doesn't exist by id %d", id)));
    }
}
