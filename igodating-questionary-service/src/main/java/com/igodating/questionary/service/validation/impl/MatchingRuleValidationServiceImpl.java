package com.igodating.questionary.service.validation.impl;

import com.igodating.questionary.exception.ValidationException;
import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.constant.QuestionAnswerType;
import com.igodating.questionary.model.constant.RuleAccessType;
import com.igodating.questionary.model.constant.RuleMatchingType;
import com.igodating.questionary.repository.MatchingRuleRepository;
import com.igodating.questionary.service.validation.MatchingRuleValidationService;
import com.igodating.questionary.service.validation.ValueFormatValidationService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchingRuleValidationServiceImpl implements MatchingRuleValidationService {

    private final MatchingRuleRepository matchingRuleRepository;

    private final ValueFormatValidationService valueFormatValidationService;

    @Override
    @Transactional(readOnly = true)
    public void validateOnCreate(MatchingRule matchingRule, Question question) {
        if (matchingRule.getId() != null) {
            throw new ValidationException("Cannot create matching rule with preset id");
        }

        checkCommonRequiredFieldsForCreateAndUpdateInMatchingRule(matchingRule, question.getAnswerType());
    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnUpdate(MatchingRule matchingRule, Question question) {
        checkCommonRequiredFieldsForCreateAndUpdateInMatchingRule(matchingRule, question.getAnswerType());

        checkMatchingRuleOnExistenceAndThrowIfDeleted(matchingRule);
    }

    private void checkCommonRequiredFieldsForCreateAndUpdateInMatchingRule(MatchingRule matchingRule, QuestionAnswerType answerType) {
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

        if (RuleAccessType.PRIVATE.equals(accessType) && StringUtils.isBlank(matchingRule.getPresetValue())) {
            throw new ValidationException("Private access should have a preset value");
        }

        if (StringUtils.isNotEmpty(matchingRule.getPresetValue())) {
            valueFormatValidationService.validateValueWithType(matchingRule.getPresetValue(), answerType);
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
