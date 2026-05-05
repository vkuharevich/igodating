package com.igodating.service.impl;

import com.igodating.dto.questionarytype.view.RecommendationView;
import com.igodating.dto.recommendations.request.RecommendationsFilter;
import com.igodating.dto.recommendations.request.RecommendationsRequest;
import com.igodating.model.FilterType;
import com.igodating.model.Question;
import com.igodating.model.Questionary;
import com.igodating.model.QuestionaryFilter;
import com.igodating.model.QuestionaryType;
import com.igodating.service.QuestionaryService;
import com.igodating.service.RecommendationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private static final Double MIN_DIST = 0.0;

    private static final Double MAX_DIST = 1.0;

    private static final Integer GAMMA = 3;

    private static final String WITH_DEFINING_QUESTIONARY = """
            WITH defining_questionary as (
            select q.id as defining_questionary_id,
            q.semantic_vector as semantic_vector,
            q.factor_vector as factor_vector
            from questionary q
            where q.id = %s
            limit 1
            )
            """;

    private static final String WITH_FILTERED_QUESTIONARIES = """
            , filtered_questionaries as (
            select a.questionary_id as questionary_id
            from answer a
            where (%s)
            group by a.questionary_id
            )
            """;

    private static final String SQL_SEMANTIC_DISTANCE = "(1.0 - ((q.semantic_vector <=> (select semantic_vector from defining_questionary limit 1)) - " + MIN_DIST + ")/(" + MAX_DIST + " - " + MIN_DIST + "))";

    private static final String SQL_FACTOR_DISTANCE = "(1.0 - ((q.factor_vector <=> (select factor_vector from defining_questionary limit 1)) - " + MIN_DIST + ")/(" + MAX_DIST + " - " + MIN_DIST + "))";

    private static final String SQL_SELECT = "select q.id as questionary_id," +
            "q.name as name," +
            "q.user_id as user_id," +
            "(%s * " + SQL_SEMANTIC_DISTANCE + ") + (%s * " + SQL_FACTOR_DISTANCE + ") as score" +
            " from questionary q ";

    private static final String JOIN_FILTERED  = """
            inner join filtered_questionaries on filtered_questionaries.questionary_id = q.id
            """;

    private static final String ANSWER_VALUE_CHOICE_IN = """
            (a.question_id = %s and a.chosen_options_keys @> %s::varchar(2)[])
            """;

    private static final String ANSWER_VALUE_NUMERIC_BETWEEN = """
            (a.question_id = %s and a.numeric_value between %s and %s)
            """;

    private static final String ANSWER_VALUE_NUMERIC_FROM = """
            (a.question_id = %s and a.numeric_value >= %s)
            """;

    private static final String ANSWER_VALUE_NUMERIC_TO = """
            (a.question_id = %s and a.numeric_value <= %s)
            """;

    private static final String WHERE_CLAUSE = """
            where q.user_id <> %s and q.status = 'PUBLISHED'
            """;

    private static final String ORDER_BY_SCORE = "order by score DESC";

    private final QuestionaryService questionaryService;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public Slice<RecommendationView> getRecommendations(RecommendationsRequest request) {
        Questionary questionary = questionaryService.getModelById(request.questionaryId());
        QuestionaryType type = questionary.getType();

        String sql = WITH_DEFINING_QUESTIONARY.formatted(request.questionaryId());
        boolean joinToFiltered = false;
        if (!CollectionUtils.isEmpty(request.filters())) {
            joinToFiltered = true;
            Map<Long, QuestionaryFilter> filterMap = type.getQuestionBlocks().stream()
                    .flatMap(q -> q.getQuestions().stream())
                    .map(Question::getFilter)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(QuestionaryFilter::getId, v -> v));

            String filteredQuestionaries = WITH_FILTERED_QUESTIONARIES;
            List<String> predicates = new ArrayList<>();
            for (RecommendationsFilter recommendationsFilter : request.filters()) {
                QuestionaryFilter filter = filterMap.get(recommendationsFilter.filterId());
                if (filter != null) {
                    Long questionId = filter.getQuestion().getId();
                    fillPredicates(filter.getType(), predicates, questionId, recommendationsFilter.optionValues(), recommendationsFilter.valueFrom(), recommendationsFilter.valueTo());
                }
            }
            for (QuestionaryFilter filter : filterMap.values()) {
                if (!filter.getIsPublic()) {
                    Long questionId = filter.getQuestion().getId();
                    fillPredicates(filter.getType(), predicates, questionId, filter.getDefaultOptionsInSetKeys(), filter.getDefaultNumericValueFrom(), filter.getDefaultNumericValueTo());
                }
            }
            String predicate = String.join(" and ", predicates);
            filteredQuestionaries = filteredQuestionaries.formatted(predicate);
            sql += filteredQuestionaries;
        }

        sql += SQL_SELECT.formatted(type.getSemanticLambdaPart(), type.getFactorLambdaPart());
        if (joinToFiltered) {
            sql += JOIN_FILTERED;
        }
        sql += WHERE_CLAUSE.formatted(questionary.getUserId());
        sql += ORDER_BY_SCORE;

        int extraLimit = request.limit() + 1;

        var query = em.createNativeQuery(sql, Object[].class);
        query.setMaxResults(extraLimit);
        query.setFirstResult(request.offset());

        List<RecommendationView> resultList = query.getResultStream().map(
                rs -> build((Object[]) rs)
        ).toList();

        return new SliceImpl<>(resultList, resultList.isEmpty() ? Pageable.unpaged() : Pageable.ofSize(resultList.size()), extraLimit == resultList.size());
    }

    private void fillPredicates(FilterType type, List<String> predicates, Long questionId, String[] optionValues, Float valueFrom, Float valueTo) {
        if (type.equals(FilterType.VALUE_IN_RANGE)) {
            if (valueFrom != null && valueTo != null) {
                predicates.add(ANSWER_VALUE_NUMERIC_BETWEEN.formatted(questionId, valueFrom, valueTo));
            } else {
                if (valueFrom != null) {
                    predicates.add(ANSWER_VALUE_NUMERIC_FROM.formatted(questionId, valueFrom));
                } else {
                    predicates.add(ANSWER_VALUE_NUMERIC_TO.formatted(questionId, valueTo));
                }
            }
        }
        if (type.equals(FilterType.CHOICE_IN_SET)) {
            predicates.add(ANSWER_VALUE_CHOICE_IN.formatted(questionId, "array[" + Arrays.stream(optionValues)
                    .map(v -> "'" + v + "'")
                    .collect(Collectors.joining(","))  + "]"));
        }
    }

    private RecommendationView build(Object[] record) {
        Long questionaryId = (Long) record[0];
        String name = (String) record[1];
        Long userId = (Long) record[2];
        Double score = distToPercent((Double) record[3]);

        return new RecommendationView(questionaryId, name, userId, score);
    }

    private Double distToPercent(Double score) {
//        double similarity = 1 - score;
//        return Math.pow(similarity, GAMMA) * 100;
        return Math.pow(score, GAMMA) * 100;
    }
}
