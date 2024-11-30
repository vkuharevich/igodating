package com.igodating.questionary.service.validation.impl;

import com.igodating.questionary.dto.filter.FullTextSearchSettings;
import com.igodating.questionary.dto.filter.UserQuestionaryFilter;
import com.igodating.questionary.dto.filter.UserQuestionaryFilterItem;
import com.igodating.questionary.exception.ValidationException;
import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.QuestionaryTemplate;
import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.model.constant.QuestionAnswerType;
import com.igodating.questionary.model.constant.RuleAccessType;
import com.igodating.questionary.repository.UserQuestionaryRepository;
import com.igodating.questionary.service.cache.QuestionaryTemplateCacheService;
import com.igodating.questionary.service.validation.UserQuestionaryFilterValidationService;
import com.igodating.questionary.service.validation.AnswerValueFormatValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserQuestionaryFilterValidationServiceImpl implements UserQuestionaryFilterValidationService {

    private final UserQuestionaryRepository userQuestionaryRepository;

    private final QuestionaryTemplateCacheService questionaryTemplateCacheService;

    private final AnswerValueFormatValidationService answerValueFormatValidationService;

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

        if (questionaryTemplate.isDeleted()) {
            throw new ValidationException("Attempt to filter by deleted template");
        }

        Map<Long, Question> questionFromTemplate = questionaryTemplate.getQuestions().stream().collect(Collectors.toMap(Question::getId, v -> v));

        if (!CollectionUtils.isEmpty(filter.userFilters())) {
            for (UserQuestionaryFilterItem userQuestionaryFilterItem : filter.userFilters()) {
                if (userQuestionaryFilterItem.isEmpty()) {
                    throw new ValidationException("Filter item is empty");
                }

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

                FullTextSearchSettings fullTextSearchSettings = userQuestionaryFilterItem.fullTextSearchSettings();
                String value = userQuestionaryFilterItem.filterValue();

                if (value != null && fullTextSearchSettings != null) {
                    throw new ValidationException("Can provide only one search type: by single value or by keywords");
                }

                if (fullTextSearchSettings != null) {
                    if (CollectionUtils.isEmpty(fullTextSearchSettings.keywords())) {
                        throw new ValidationException("Fulltext search is provided but keywords are missed");
                    }

                    if (!QuestionAnswerType.FREE_FORM.equals(matchedQuestionFromTemplate.getAnswerType())) {
                        throw new ValidationException("Fulltext search is acceptable only for free form");
                    }
                }

                answerValueFormatValidationService.validateValueWithQuestion(value, matchedQuestionFromTemplate);
            }
        }
    }
}
