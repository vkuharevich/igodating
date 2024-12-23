package com.igodating.questionary.service.validation.impl;

import com.igodating.commons.exception.ValidationException;
import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.MatchingRuleDefaultValues;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.constant.QuestionAnswerType;
import com.igodating.questionary.model.constant.RuleAccessType;
import com.igodating.questionary.model.constant.RuleMatchingType;
import com.igodating.questionary.repository.MatchingRuleRepository;
import com.igodating.questionary.service.validation.MatchingRuleValidationService;
import com.igodating.questionary.service.validation.AnswerValueFormatValidationService;
import com.igodating.questionary.util.val.ValuesMatchingChecker;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MatchingRuleValidationServiceImpl implements MatchingRuleValidationService {

    private final MatchingRuleRepository matchingRuleRepository;

    private final AnswerValueFormatValidationService answerValueFormatValidationService;

    private final ValuesMatchingChecker valuesMatchingChecker;

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

        if (RuleMatchingType.SEMANTIC_RANGING.equals(matchingType) && matchingRule.getDefaultValues() != null) {
            throw new ValidationException("Cannot provide default value for Semantic Ranging");
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

        if (RuleMatchingType.MORE_THEN.equals(matchingType) && !answerType.equals(QuestionAnswerType.NUMERIC)) {
            throw new ValidationException("More then can be applied only to numeric");
        }

        if (RuleMatchingType.LESS_THEN.equals(matchingType) && !answerType.equals(QuestionAnswerType.NUMERIC)) {
            throw new ValidationException("Less then can be applied only to numeric");
        }

        boolean answerTypeIsAcceptableForEquals = !(answerType.equals(QuestionAnswerType.FREE_FORM) || answerType.equals(QuestionAnswerType.NUMERIC) || answerType.equals(QuestionAnswerType.CHOICE));
        if (RuleMatchingType.EQUALS.equals(matchingType) && answerTypeIsAcceptableForEquals) {
            throw new ValidationException("Equals can be applied only to free form, numeric or one-choice");
        }
        if (RuleMatchingType.NOT_EQUALS.equals(matchingType) && answerTypeIsAcceptableForEquals) {
            throw new ValidationException("Not equals can be applied only to free form, numeric or one-choice");
        }

        if (RuleAccessType.PRIVATE.equals(accessType) && !Boolean.TRUE.equals(matchingRule.getIsMandatoryForMatching())) {
            throw new ValidationException("Private access should be mandatory for matching");
        }

        if (Boolean.TRUE.equals(matchingRule.getIsMandatoryForMatching()) && matchingRule.getDefaultValues() == null) {
            throw new ValidationException("Default values should be set if rule is mandatory for match");
        }

        if (matchingRule.getDefaultValues() != null) {
            checkDefaultValues(matchingRule.getDefaultValues(), question);
        }
    }

    private void checkMatchingRuleOnExistenceAndThrowIfDeleted(MatchingRule matchingRule) {
        Long id = matchingRule.getId();
        if (id == null) {
            throw new ValidationException("Id is required for rule updating");
        }

        matchingRuleRepository.findById(id).orElseThrow(() -> new ValidationException(String.format("Matching rule doesn't exist by id %d", id)));
    }

    private void checkDefaultValues(MatchingRuleDefaultValues defaultValues, Question question) {
        if (!CollectionUtils.isEmpty(defaultValues.getCases())) {
            Set<String> alreadyTouchedCases = new HashSet<>();
            defaultValues.getCases().forEach((defaultValCase) -> {
                String when = defaultValCase.getWhen();
                String then = defaultValCase.getThen();

                answerValueFormatValidationService.validateValueWithQuestion(when, question);
                answerValueFormatValidationService.validateValueWithQuestion(then, question);

                alreadyTouchedCases.forEach(alreadyTouchedKey -> {
                    if (valuesMatchingChecker.match(alreadyTouchedKey, when, question)) {
                        throw new ValidationException(String.format("When clause %s already defined", when));
                    }
                });

                alreadyTouchedCases.add(when);
            });
        }

        if (StringUtils.isBlank(defaultValues.getDefaultValue())) {
            throw new ValidationException("Default value is necessary for default values block");
        }

        answerValueFormatValidationService.validateValueWithQuestion(defaultValues.getDefaultValue(), question);
    }
}
