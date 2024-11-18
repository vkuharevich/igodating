package com.igodating.questionary.service.impl;

import com.igodating.questionary.dto.CursorResponse;
import com.igodating.questionary.dto.filter.UserQuestionaryFilter;
import com.igodating.questionary.dto.userquestionary.UserQuestionaryShortView;
import com.igodating.questionary.exception.ValidationException;
import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.QuestionaryTemplate;
import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.model.constant.RuleAccessType;
import com.igodating.questionary.model.constant.RuleMatchingType;
import com.igodating.questionary.repository.UserQuestionaryRepository;
import com.igodating.questionary.service.UserQuestionaryFilterService;
import com.igodating.questionary.service.cache.QuestionaryTemplateCacheService;
import com.igodating.questionary.service.validation.UserQuestionaryFilterValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserQuestionaryFilterServiceImpl implements UserQuestionaryFilterService {

    private final UserQuestionaryRepository userQuestionaryRepository;

    private final QuestionaryTemplateCacheService questionaryTemplateCacheService;

    private final UserQuestionaryFilterValidationService userQuestionaryFilterValidationService;


    @Override
    @Transactional(readOnly = true)
    public CursorResponse<UserQuestionaryShortView> findByCursorForUserId(UserQuestionaryFilter filter, String userId) {
        userQuestionaryFilterValidationService.validateUserQuestionaryFilter(filter, userId);

        UserQuestionary forQuestionary = userQuestionaryRepository.findById(filter.forUserQuestionaryId()).orElseThrow(() -> new ValidationException("Entity not found by id"));

        QuestionaryTemplate questionaryTemplate = questionaryTemplateCacheService.getById(forQuestionary.getQuestionaryTemplateId());

        Map<Long, MatchingRule> matchingRuleQuestionIdMap = questionaryTemplate.getQuestions().stream().map(Question::getMatchingRule).collect(Collectors.toMap(MatchingRule::getQuestionId, v -> v));

        if (matchingRuleQuestionIdMap.isEmpty()) {
            throw new RuntimeException("Matching rules don't exist for template");
        }

        List<MatchingRule> privateMatchingRules = matchingRuleQuestionIdMap.values().stream().filter(mr -> RuleAccessType.PRIVATE.equals(mr.getAccessType())).toList();
        List<MatchingRule> userMatchingRules = filter.userFilters().stream().map(questionFilter -> matchingRuleQuestionIdMap.get(questionFilter.questionId())).toList();
        boolean privateMatchingRulesAreSemanticOnly = privateMatchingRules.stream().allMatch(mr -> RuleMatchingType.SEMANTIC_RANGING.equals(mr.getMatchingType()));

        if (userMatchingRules.isEmpty() && privateMatchingRulesAreSemanticOnly) {
            // можем идти, не залезая в ответы
        } else {
            // придется лезть в ответы
        }

        return null;
    }
}
