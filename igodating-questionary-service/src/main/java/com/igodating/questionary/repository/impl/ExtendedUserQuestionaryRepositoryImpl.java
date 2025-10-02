package com.igodating.questionary.repository.impl;

import com.igodating.questionary.constant.CommonConstants;
import com.igodating.questionary.constant.SimilarityCalculatingOperator;
import com.igodating.questionary.dto.filter.UserQuestionaryFilterItem;
import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.QuestionaryTemplate;
import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.model.UserQuestionaryAnswer;
import com.igodating.questionary.model.constant.RuleMatchingType;
import com.igodating.questionary.model.view.UserQuestionaryRecommendationView;
import com.igodating.questionary.repository.ExtendedUserQuestionaryRepository;
import com.igodating.questionary.repository.QuestionaryTemplateRepository;
import com.igodating.questionary.util.tsquery.TsQueryConverter;
import com.igodating.questionary.util.val.DefaultValueExtractor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.util.Pair;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
public class ExtendedUserQuestionaryRepositoryImpl implements ExtendedUserQuestionaryRepository {

    private static final String SELECT_ROOT_WITH_SIMILARITY_CALC_FORMAT = """
            select uq.id, uq.user_id, cast(%s as float) as calculated_similarity from user_questionary uq
            """;

    private static final String JOIN_TO_ANSWERS = """
            inner join user_questionary_answer uqa on uq.id = uqa.user_questionary_id
            """;

    private static final String ANSWER_VALUE_EQUALITY_FORMAT = """
            uqa.value = :eqValue_%d
            """;

    private static final String ANSWER_VALUE_NOT_EQUALITY_FORMAT = """
            uqa.value <> :notEqValue_%d
            """;

    private static final String ANSWER_VALUE_NUMERIC_IN_RANGE_FORMAT = """
            uqa.value::numeric between :rangeFrom_%d and :rangeTo_%d
            """;

    private static final String ANSWER_VALUE_NUMERIC_MORE_THEN_FORMAT = """
            uqa.value::numeric > :moreThenValue_%d
            """;

    private static final String ANSWER_VALUE_NUMERIC_LESS_THEN_FORMAT = """
            uqa.value::numeric < :lessThenValue_%d
            """;

    private static final String ANSWER_VALUE_ARRAY_IN_SET_FORMAT =
            "string_to_array(uqa.value, '" + CommonConstants.VALUE_SPLITTER + "') @> :arrayValue_%d";

    private static final String TS_QUERY_ANSWER_VALUE_LIKING_FORMAT = """
            uqa.ts_vector_value @@ to_tsquery(:tsQuery_%d)
            """;

    private static final String QUESTION_PREDICATE_FORMAT = """
            %s and uqa.question_id = %d
            """;

    private static final String QUESTIONARY_ID_NOT_EQUALS = """
            uq.id <> :targetQuestionaryId
            """;

    private static final String QUESTIONARY_IS_NOT_DELETED = """
            uq.deleted_at is null
            """;

    private static final String EUCLID_SIMILARITY = """
            uq.embedding <-> (:targetEmbedding)::vector
            """;

    private static final String COSINE_SIMILARITY = """
            uq.embedding <=> (:targetEmbedding)::vector
            """;

    private static final String SCALAR_SIMILARITY = """
            uq.embedding <#> (:targetEmbedding)::vector
            """;

    private static final String ORDER_BY_SIMILARITY_ASC = """
            order by calculated_similarity ASC
            """;

    private static final String ORDER_BY_SIMILARITY_DESC = """
            order by calculated_similarity DESC
            """;

    private static final String GROUP_BY_QUESTIONARY_ID = """
            group by uq.id
            """;

    private final TsQueryConverter tsQueryConverter;

    private final DefaultValueExtractor defaultValueExtractor;

    @PersistenceContext
    private EntityManager em;

    @Override
    public Slice<UserQuestionaryRecommendationView> findRecommendations(UserQuestionary forQuestionary, List<UserQuestionaryFilterItem> userFilters, SimilarityCalculatingOperator similarityCalculatingOperator, int limit, int offset) {
        QuestionaryTemplate questionaryTemplate = em.find(QuestionaryTemplate.class, forQuestionary.getQuestionaryTemplateId(), Map.of(CommonConstants.FETCH_GRAPH_PROPERTY_KEY, em.getEntityGraph("questionaryTemplate")));
        List<Question> questionsFromTemplate = questionaryTemplate.getQuestions();

        Map<Long, MatchingRule> matchingRuleQuestionIdMap = questionsFromTemplate.stream().map(Question::getMatchingRule).collect(Collectors.toMap(MatchingRule::getQuestionId, v -> v));

        if (matchingRuleQuestionIdMap.isEmpty()) {
            throw new RuntimeException("Matching rules don't exist for template");
        }

        List<MatchingRule> mandatoryMatchingRules = matchingRuleQuestionIdMap.values().stream().filter(mr -> Boolean.TRUE.equals(mr.getIsMandatoryForMatching())).toList();
        List<MatchingRule> userMatchingRules = userFilters == null ? new ArrayList<>() : userFilters.stream().map(questionFilter -> matchingRuleQuestionIdMap.get(questionFilter.questionId())).toList();
        boolean semanticIsPresent = mandatoryMatchingRules.stream().anyMatch(mr -> RuleMatchingType.SEMANTIC_RANGING.equals(mr.getMatchingType()));
        boolean mandatoryMatchingRulesAreSemanticOnly = mandatoryMatchingRules.stream().allMatch(mr -> RuleMatchingType.SEMANTIC_RANGING.equals(mr.getMatchingType()));

        List<String> joins = new ArrayList<>();
        List<String> predicates = new ArrayList<>();
        List<String> havingPredicates = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        String groupBy = "";
        String orderBy = "";

        StringBuilder sql = new StringBuilder();

        if (semanticIsPresent) {
            sql.append(getSelectClauseBySimilarityCalculatingOperator(similarityCalculatingOperator));
            orderBy = getOrderBySimilarityCalculatingOperator(similarityCalculatingOperator);
            params.put("targetEmbedding", forQuestionary.getEmbedding());
        } else {
            sql.append(String.format(SELECT_ROOT_WITH_SIMILARITY_CALC_FORMAT, getDefaultSimilarityValueBySimilarityCalculatingOperator(similarityCalculatingOperator)));
        }

        if (userMatchingRules.isEmpty() && mandatoryMatchingRulesAreSemanticOnly) {
            // можем идти, не залезая в ответы

            predicates.add(QUESTIONARY_ID_NOT_EQUALS);
            params.put("targetQuestionaryId", forQuestionary.getId());

            predicates.add(QUESTIONARY_IS_NOT_DELETED);
        } else {
            // придется лезть в ответы
            joins.add(JOIN_TO_ANSWERS);

            groupBy = GROUP_BY_QUESTIONARY_ID;

            Set<MatchingRule> allRules = new HashSet<>(mandatoryMatchingRules);
            allRules.addAll(userMatchingRules);
            Pair<String, Map<String, Object>> questionsOrPredicateAndParamMap = toQuestionsOrPredicateAndParamMap(allRules, userFilters, questionsFromTemplate, forQuestionary);

            if (!questionsOrPredicateAndParamMap.getFirst().isEmpty()) {
                predicates.add(questionsOrPredicateAndParamMap.getFirst());
                params.putAll(questionsOrPredicateAndParamMap.getSecond());
            }

            havingPredicates.add(QUESTIONARY_ID_NOT_EQUALS);
            params.put("targetQuestionaryId", forQuestionary.getId());

            havingPredicates.add(QUESTIONARY_IS_NOT_DELETED);
        }

        for (String join : joins) {
            sql.append(join);
        }

        if (!predicates.isEmpty()) {
            sql.append(" where ");
            sql.append(String.join(" and ", predicates.stream().map(p -> " (" + p + ") ").collect(Collectors.toSet())));
        }

        sql.append(groupBy);

        if (!havingPredicates.isEmpty()) {
            sql.append(" having ");
            sql.append(String.join(" and ", havingPredicates.stream().map(p -> " (" + p + ") ").collect(Collectors.toSet())));
        }

        sql.append(orderBy);

        log.debug("SQL for recommendations request are generated {}", sql);

        var query = em.createNativeQuery(sql.toString(), Object[].class);
        params.forEach(query::setParameter);

        int limitWithExtra = limit + 1;
        query.setFirstResult(offset);
        query.setMaxResults(limitWithExtra);

        List<UserQuestionaryRecommendationView> resultList = query.getResultStream().map(
                rs -> build((Object[]) rs)
        ).toList();

        return new SliceImpl<>(resultList, resultList.isEmpty() ? Pageable.unpaged() : Pageable.ofSize(resultList.size()), limitWithExtra == resultList.size());
    }

    private String getDefaultSimilarityValueBySimilarityCalculatingOperator(SimilarityCalculatingOperator similarityCalculatingOperator) {
        switch (similarityCalculatingOperator) {
            case EUCLID -> {
                return "0";
            }
            case COSINE -> {
                return "1";
            }
            case SCALAR -> {
                return "-1";
            }
            default -> throw new RuntimeException(String.format("Cannot perform operator %s", similarityCalculatingOperator));
        }
    }

    private String getSelectClauseBySimilarityCalculatingOperator(SimilarityCalculatingOperator similarityCalculatingOperator) {
        switch (similarityCalculatingOperator) {
            case EUCLID -> {
                return String.format(SELECT_ROOT_WITH_SIMILARITY_CALC_FORMAT, EUCLID_SIMILARITY);
            }
            case COSINE -> {
                return String.format(SELECT_ROOT_WITH_SIMILARITY_CALC_FORMAT, COSINE_SIMILARITY);
            }
            case SCALAR -> {
                return String.format(SELECT_ROOT_WITH_SIMILARITY_CALC_FORMAT, SCALAR_SIMILARITY);
            }
            default -> throw new RuntimeException(String.format("Cannot perform operator %s", similarityCalculatingOperator));
        }
    }

    private String getOrderBySimilarityCalculatingOperator(SimilarityCalculatingOperator similarityCalculatingOperator) {
        if (SimilarityCalculatingOperator.COSINE.equals(similarityCalculatingOperator)) {
            return ORDER_BY_SIMILARITY_DESC;
        }

        return ORDER_BY_SIMILARITY_ASC;
    }

    private Pair<String, Map<String, Object>> toQuestionsOrPredicateAndParamMap(Set<MatchingRule> matchingRules,
                                                                                List<UserQuestionaryFilterItem> userFilters,
                                                                                List<Question> questionsFromTemplate,
                                                                                UserQuestionary forQuestionary) {
        List<String> predicates = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        Map<Long, UserQuestionaryFilterItem> userFilterValuesByQuestionId = CollectionUtils.isEmpty(userFilters) ? new HashMap<>() :userFilters.stream()
                .collect(Collectors.toMap(UserQuestionaryFilterItem::questionId, Function.identity()));
        Map<Long, String> userAnswersByQuestionId = forQuestionary.getAnswers()
                .stream()
                .collect(Collectors.toMap(UserQuestionaryAnswer::getQuestionId, UserQuestionaryAnswer::getValue));
        Map<Long, Question> questionFromTemplateMap = questionsFromTemplate.stream().collect(Collectors.toMap(Question::getId, v -> v));

        for (MatchingRule matchingRule : matchingRules) {
            if (RuleMatchingType.SEMANTIC_RANGING.equals(matchingRule.getMatchingType())) {
                continue;
            }

            String userValue = null;

            Long questionId = matchingRule.getQuestionId();
            UserQuestionaryFilterItem filterItem = userFilterValuesByQuestionId.get(questionId);
            if (filterItem != null) {
                if (RuleMatchingType.LIKE.equals(matchingRule.getMatchingType())) {
                    userValue = tsQueryConverter.strToTsQuery(filterItem.filterValue());
                } else {
                    userValue = filterItem.filterValue();
                }
            }

            Pair<String, Map<String, Object>> questionPredicateAndParamMap = toQuestionPredicateAndParamMap(matchingRule, userValue, questionFromTemplateMap.get(questionId), userAnswersByQuestionId.get(questionId));
            if (questionPredicateAndParamMap == null) {
                continue;
            }

            predicates.add(String.format(QUESTION_PREDICATE_FORMAT, questionPredicateAndParamMap.getFirst(), questionId));
            params.putAll(questionPredicateAndParamMap.getSecond());
        }

        if (predicates.isEmpty()) {
            return Pair.of("", new HashMap<>());
        }

        return Pair.of(predicates.stream().map(p -> new StringBuilder().append("(").append(p).append(")"))
                .collect(Collectors.joining(" or ")), params);
    }

    private Pair<String, Map<String, Object>> toQuestionPredicateAndParamMap(MatchingRule matchingRule, String userValue, Question question, String answer) {
        assert matchingRule.getMatchingType() != RuleMatchingType.SEMANTIC_RANGING;
        Long questionId = matchingRule.getQuestionId();

        String value = userValue == null ? defaultValueExtractor.extractDefaultValueForMatchingByAnswer(answer, question) : userValue;
        if (value == null) {
            if (answer == null) {
                return null;
            }

            value = answer;
        }

        String predicate = "";
        Map<String, Object> params = new HashMap<>();

        switch (matchingRule.getMatchingType()) {
            case EQUALS -> {
                predicate = String.format(ANSWER_VALUE_EQUALITY_FORMAT, questionId);
                params.put(String.format("eqValue_%d", questionId), value);
            }

            case NOT_EQUALS -> {
                predicate = String.format(ANSWER_VALUE_NOT_EQUALITY_FORMAT, questionId);
                params.put(String.format("notEqValue_%d", questionId), value);
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

            case MORE_THEN -> {
                predicate = String.format(ANSWER_VALUE_NUMERIC_MORE_THEN_FORMAT, questionId);
                params.put(String.format("moreThenValue_%d", questionId), new BigDecimal(value));
            }

            case LESS_THEN -> {
                predicate = String.format(ANSWER_VALUE_NUMERIC_LESS_THEN_FORMAT, questionId);
                params.put(String.format("lessThenValue_%d", questionId), new BigDecimal(value));
            }

            case IN_SET -> {
                String[] stringArray = value.split(CommonConstants.VALUE_SPLITTER);
                predicate = String.format(ANSWER_VALUE_ARRAY_IN_SET_FORMAT, questionId);
                params.put(String.format("arrayValue_%d", questionId), stringArray);
            }
        }

        return Pair.of(predicate, params);
    }

    private UserQuestionaryRecommendationView build(Object[] result) {
        Long userQuestionaryId = (Long) result[0];
        String userId = (String) result[1];
        Double similarity = (Double) result[2];

        return new UserQuestionaryRecommendationView(userQuestionaryId, userId, similarity);
    }
}
