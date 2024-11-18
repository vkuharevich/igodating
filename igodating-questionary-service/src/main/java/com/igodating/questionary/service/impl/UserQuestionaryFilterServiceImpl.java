package com.igodating.questionary.service.impl;

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
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserQuestionaryFilterServiceImpl implements UserQuestionaryFilterService {

    private static final String SELECT_ROOT = """
            select uq.id from user_questionary uq
            """;

    private static final String JOIN_TO_ANSWERS = """
            inner join user_questionary_answer uqa on uq.id = uqa.user_questionary_id
            """;

    private static final String ANSWER_VALUE_EQUALITY = """
            uqa.value = {:value}
            """;

    private static final String ANSWER_VALUE_NUMERIC_IN_RANGE = """
            uqa.value::numeric between {:rangeFrom} and {:rangeTo}
            """;

    private static final String ANSWER_VALUE_ARRAY_IN_SET = """
            string_to_array(uqa.value, ';') @> {:arrayValue}
            """;

    private static final String TS_QUERY_ANSWER_VALUE_LIKING = """
            uqa.ts_vector_value @@ to_tsquery({:tsQuery})
            """;

    private static final String ORDER_BY_QUESTIONARY_EMBEDDING = """
            order by uq.embedding <-> (:targetEmbedding)::vector DESC
            """;

    private static final String LIMIT_OFFSET_CLAUSE_FORMAT = """
            limit %d offset %d
            """;

    private final UserQuestionaryRepository userQuestionaryRepository;

    private final QuestionaryTemplateCacheService questionaryTemplateCacheService;

    private final UserQuestionaryFilterValidationService userQuestionaryFilterValidationService;


    @Override
    @Transactional(readOnly = true)
    public Page<UserQuestionaryShortView> findByCursorForUserId(UserQuestionaryFilter filter, String userId) {
        userQuestionaryFilterValidationService.validateUserQuestionaryFilter(filter, userId);

        UserQuestionary forQuestionary = userQuestionaryRepository.findById(filter.forUserQuestionaryId()).orElseThrow(() -> new ValidationException("Entity not found by id"));

        QuestionaryTemplate questionaryTemplate = questionaryTemplateCacheService.getById(forQuestionary.getQuestionaryTemplateId());

        Map<Long, MatchingRule> matchingRuleQuestionIdMap = questionaryTemplate.getQuestions().stream().map(Question::getMatchingRule).collect(Collectors.toMap(MatchingRule::getQuestionId, v -> v));

        if (matchingRuleQuestionIdMap.isEmpty()) {
            throw new RuntimeException("Matching rules don't exist for template");
        }

        List<MatchingRule> privateMatchingRules = matchingRuleQuestionIdMap.values().stream().filter(mr -> RuleAccessType.PRIVATE.equals(mr.getAccessType())).toList();
        List<MatchingRule> userMatchingRules = filter.userFilters().stream().map(questionFilter -> matchingRuleQuestionIdMap.get(questionFilter.questionId())).toList();
        boolean semanticIsPresent = privateMatchingRules.stream().anyMatch(mr -> RuleMatchingType.SEMANTIC_RANGING.equals(mr.getMatchingType()));
        boolean privateMatchingRulesAreSemanticOnly = privateMatchingRules.stream().allMatch(mr -> RuleMatchingType.SEMANTIC_RANGING.equals(mr.getMatchingType()));

        List<String> joins = new ArrayList<>();
        List<String> predicates = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (userMatchingRules.isEmpty() && privateMatchingRulesAreSemanticOnly) {
            // можем идти, не залезая в ответы

        } else {
            // придется лезть в ответы
            joins.add(JOIN_TO_ANSWERS);
        }

        return null;
    }
}
