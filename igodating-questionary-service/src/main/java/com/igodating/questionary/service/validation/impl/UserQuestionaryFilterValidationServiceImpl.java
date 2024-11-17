package com.igodating.questionary.service.validation.impl;

import com.igodating.questionary.dto.filter.UserQuestionaryFilter;
import com.igodating.questionary.dto.filter.UserQuestionaryFilterItem;
import com.igodating.questionary.exception.ValidationException;
import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.QuestionaryTemplate;
import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.model.constant.RuleAccessType;
import com.igodating.questionary.repository.UserQuestionaryRepository;
import com.igodating.questionary.service.cache.QuestionaryTemplateCacheService;
import com.igodating.questionary.service.validation.UserQuestionaryFilterValidationService;
import com.igodating.questionary.service.validation.ValueFormatValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserQuestionaryFilterValidationServiceImpl implements UserQuestionaryFilterValidationService {

    private final UserQuestionaryRepository userQuestionaryRepository;

    private final QuestionaryTemplateCacheService questionaryTemplateCacheService;

    private final ValueFormatValidationService valueFormatValidationService;

    @Override
    @Transactional(readOnly = true)
    public void validateUserQuestionaryFilter(UserQuestionaryFilter filter, String userId) {
        UserQuestionary forQuestionary = userQuestionaryRepository.findById(filter.forUserQuestionaryId()).orElseThrow(() -> new ValidationException("Entity not found by id"));

        if (!Objects.equals(forQuestionary.getUserId(), userId)) {
            throw new ValidationException("Wrong user id");
        }

        QuestionaryTemplate questionaryTemplate = questionaryTemplateCacheService.getById(forQuestionary.getQuestionaryTemplateId());

        if (questionaryTemplate == null) {
            throw new ValidationException("Template is not in template");
        }

        Map<Long, Question> questionFromTemplate = questionaryTemplate.getQuestions().stream().collect(Collectors.toMap(Question::getId, v -> v));

        for (UserQuestionaryFilterItem userQuestionaryFilterItem : filter.userFilters()) {
            Question matchedQuestionFromTemplate = questionFromTemplate.get(userQuestionaryFilterItem.questionId());

            if (matchedQuestionFromTemplate == null) {
                throw new ValidationException("Question not in template");
            }

            MatchingRule matchingRule = matchedQuestionFromTemplate.getMatchingRule();

            if (matchingRule == null) {
                throw new ValidationException("Matching rule doesn't exist");
            }

            if (RuleAccessType.PRIVATE.equals(matchingRule.getAccessType())) {
                throw new ValidationException("Private access");
            }

            valueFormatValidationService.validateValueWithType(userQuestionaryFilterItem.filterValue(), matchedQuestionFromTemplate.getAnswerType());
        }
    }
}
