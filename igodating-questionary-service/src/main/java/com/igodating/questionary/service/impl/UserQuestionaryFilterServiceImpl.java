package com.igodating.questionary.service.impl;

import com.igodating.questionary.constant.CommonConstants;
import com.igodating.questionary.dto.filter.UserQuestionaryFilter;
import com.igodating.questionary.dto.filter.UserQuestionaryFilterItem;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserQuestionaryFilterServiceImpl implements UserQuestionaryFilterService {

    private static final String SELECT_ROOT = """
            select uq.id, uq.user_id from user_questionary uq
            """;

    private static final String JOIN_TO_ANSWERS = """
            inner join user_questionary_answer uqa on uq.id = uqa.user_questionary_id
            """;

    private static final String ANSWER_VALUE_EQUALITY_FORMAT = """
            uqa.value = :value_%d
            """;

    private static final String ANSWER_VALUE_NUMERIC_IN_RANGE_FORMAT = """
            uqa.value::numeric between :rangeFrom_%d and :rangeTo_%d
            """;

    private static final String ANSWER_VALUE_ARRAY_IN_SET_FORMAT = """
            string_to_array(uqa.value, ';') @> :arrayValue_%d
            """;

    private static final String TS_QUERY_ANSWER_VALUE_LIKING_FORMAT = """
            uqa.ts_vector_value @@ to_tsquery(:tsQuery_%d)
            """;

    private static final String QUESTION_PREDICATE_FORMAT = """
            %s and uqa.question_id = %d
            """;

    private static final String QUESTIONARY_ID_NOT_EQUALS = """
            uq.id <> :targetQuestionaryId
            """;

    private static final String ORDER_BY_QUESTIONARY_EMBEDDING = """
            order by uq.embedding <-> (:targetEmbedding)::vector DESC
            """;

    private static final String GROUP_BY_QUESTIONARY_ID = """
            group by uq.id
            """;

    private final UserQuestionaryRepository userQuestionaryRepository;

    private final QuestionaryTemplateCacheService questionaryTemplateCacheService;

    private final UserQuestionaryFilterValidationService userQuestionaryFilterValidationService;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public Slice<UserQuestionaryShortView> findByFilter(UserQuestionaryFilter filter, String userId) {
        userQuestionaryFilterValidationService.validateUserQuestionaryFilter(filter, userId);

        UserQuestionary forQuestionary = userQuestionaryRepository.findById(filter.forUserQuestionaryId()).orElseThrow(() -> new ValidationException("Entity not found by id"));

        QuestionaryTemplate questionaryTemplate = questionaryTemplateCacheService.getById(forQuestionary.getQuestionaryTemplateId());

        Map<Long, MatchingRule> matchingRuleQuestionIdMap = questionaryTemplate.getQuestions().stream().map(Question::getMatchingRule).collect(Collectors.toMap(MatchingRule::getQuestionId, v -> v));

        if (matchingRuleQuestionIdMap.isEmpty()) {
            throw new RuntimeException("Matching rules don't exist for template");
        }

        List<MatchingRule> privateMatchingRules = matchingRuleQuestionIdMap.values().stream().filter(mr -> RuleAccessType.PRIVATE.equals(mr.getAccessType())).toList();
        List<MatchingRule> userMatchingRules = filter.userFilters() == null ? new ArrayList<>() : filter.userFilters().stream().map(questionFilter -> matchingRuleQuestionIdMap.get(questionFilter.questionId())).toList();
        boolean semanticIsPresent = privateMatchingRules.stream().anyMatch(mr -> RuleMatchingType.SEMANTIC_RANGING.equals(mr.getMatchingType()));
        boolean privateMatchingRulesAreSemanticOnly = privateMatchingRules.stream().allMatch(mr -> RuleMatchingType.SEMANTIC_RANGING.equals(mr.getMatchingType()));

        StringBuilder sql = new StringBuilder(SELECT_ROOT);

        List<String> joins = new ArrayList<>();
        List<String> predicates = new ArrayList<>();
        List<String> havingPredicates = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        String groupBy = "";
        String orderBy = "";

        if (userMatchingRules.isEmpty() && privateMatchingRulesAreSemanticOnly) {
            // можем идти, не залезая в ответы
            orderBy = ORDER_BY_QUESTIONARY_EMBEDDING;
            params.put("targetEmbedding", forQuestionary.getEmbedding());

            predicates.add(QUESTIONARY_ID_NOT_EQUALS);
            params.put("targetQuestionaryId", forQuestionary.getId());
        } else {
            // придется лезть в ответы
            joins.add(JOIN_TO_ANSWERS);

            groupBy = GROUP_BY_QUESTIONARY_ID;

            if (semanticIsPresent) {
                orderBy = ORDER_BY_QUESTIONARY_EMBEDDING;
                params.put("targetEmbedding", forQuestionary.getEmbedding());
            }

            List<MatchingRule> allRules = new ArrayList<>(privateMatchingRules);
            allRules.addAll(userMatchingRules);
            Pair<String, Map<String, Object>> questionsOrPredicateAndParamMap = toQuestionsOrPredicateAndParamMap(allRules, filter.userFilters());

            if (!questionsOrPredicateAndParamMap.getFirst().isEmpty()) {
                predicates.add(questionsOrPredicateAndParamMap.getFirst());
                params.putAll(questionsOrPredicateAndParamMap.getSecond());
            }

            havingPredicates.add(QUESTIONARY_ID_NOT_EQUALS);
            params.put("targetQuestionaryId", forQuestionary.getId());
        }

        for (String join : joins) {
            sql.append(join);
        }

        if (!predicates.isEmpty()) {
            sql.append(" where ");
            sql.append(String.join(" and ", predicates));
        }

        sql.append(groupBy);

        if (!havingPredicates.isEmpty()) {
            sql.append(" having ");
            for (String havingPredicate : havingPredicates) {
                sql.append(havingPredicate);
            }
        }

        sql.append(orderBy);

        var query = em.createNativeQuery(sql.toString(), Object[].class);
        params.forEach(query::setParameter);

        int limitWithExtra = filter.limit() + 1;
        query.setFirstResult(filter.offset());
        query.setMaxResults(limitWithExtra);

        List<UserQuestionaryShortView> resultList = query.getResultStream().map(rs -> build((Object[]) rs)).toList();

        return new SliceImpl<>(resultList, Pageable.ofSize(resultList.size()), limitWithExtra == resultList.size());
    }

    private Pair<String, Map<String, Object>> toQuestionsOrPredicateAndParamMap(List<MatchingRule> matchingRules, List<UserQuestionaryFilterItem> userFilters) {
        List<String> predicates = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        Map<Long, String> userFilterValuesByQuestionId = userFilters.stream().collect(Collectors.toMap(UserQuestionaryFilterItem::questionId, UserQuestionaryFilterItem::filterValue));

        for (MatchingRule matchingRule : matchingRules) {
            if (RuleMatchingType.SEMANTIC_RANGING.equals(matchingRule.getMatchingType())) {
                continue;
            }
            Long questionId = matchingRule.getQuestionId();
            Pair<String, Map<String, Object>> questionPredicateAndParamMap = toQuestionPredicateAndParamMap(matchingRule, userFilterValuesByQuestionId.get(questionId));
            predicates.add(String.format(QUESTION_PREDICATE_FORMAT, questionPredicateAndParamMap.getFirst(), questionId));
            params.putAll(questionPredicateAndParamMap.getSecond());
        }

        if (predicates.isEmpty()) {
            return Pair.of("", new HashMap<>());
        }

        return Pair.of(predicates.stream().map(p -> new StringBuilder().append("(").append(p).append(")")).collect(Collectors.joining(" or ")), params);
    }

    private Pair<String, Map<String, Object>> toQuestionPredicateAndParamMap(MatchingRule matchingRule, String userValue) {
        assert matchingRule.getMatchingType() != RuleMatchingType.SEMANTIC_RANGING;
        Long questionId = matchingRule.getQuestionId();
        String value = userValue == null ? matchingRule.getPresetValue() : userValue;
        String predicate = "";
        Map<String, Object> params = new HashMap<>();

        switch (matchingRule.getMatchingType()) {
            case EQUALS -> {
                predicate = String.format(ANSWER_VALUE_EQUALITY_FORMAT, questionId);
                params.put(String.format("value_%d", questionId), value);
            }

            case LIKE -> {
                predicate = String.format(TS_QUERY_ANSWER_VALUE_LIKING_FORMAT, questionId);
                params.put(String.format("tsQuery_%d", questionId), value);
            }

            case IN_RANGE -> {
                String[] fromToStringValues = value.split(CommonConstants.VALUE_SPLITTER);
                predicate = String.format(ANSWER_VALUE_NUMERIC_IN_RANGE_FORMAT, questionId, questionId);
                params.put(String.format("rangeFrom_%d", questionId), new BigDecimal(fromToStringValues[0]));
                params.put(String.format("rangeTo_%d", questionId), new BigDecimal(fromToStringValues[1]));
            }

            case IN_SET -> {
                String[] stringArray = value.split(CommonConstants.VALUE_SPLITTER);
                predicate = String.format(ANSWER_VALUE_ARRAY_IN_SET_FORMAT, questionId);
                params.put(String.format("arrayValue_%d", questionId), stringArray);
            }
        }

        return Pair.of(predicate, params);
    }

    private UserQuestionaryShortView build(Object[] result) {
        return new UserQuestionaryShortView((Long) result[0], (String) result[1]);
    }
}
