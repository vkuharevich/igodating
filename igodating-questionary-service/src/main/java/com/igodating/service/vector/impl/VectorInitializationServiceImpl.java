package com.igodating.service.vector.impl;

import com.igodating.constant.Constants;
import com.igodating.model.Answer;
import com.igodating.model.AnswerOption;
import com.igodating.model.Question;
import com.igodating.model.Questionary;
import com.igodating.model.QuestionaryType;
import com.igodating.repository.QuestionaryRepository;
import com.igodating.service.QuestionaryTypeService;
import com.igodating.service.vector.FactorSimilarityAnalyzeTask;
import com.igodating.service.vector.FactorSimilarityService;
import com.igodating.service.vector.SemanticSimilarityAnswerAnalyzeTask;
import com.igodating.service.vector.SemanticSimilarityService;
import com.igodating.service.vector.VectorInitializationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class VectorInitializationServiceImpl implements VectorInitializationService {

    private final QuestionaryTypeService questionaryTypeCache;

    private final QuestionaryRepository questionaryRepository;

    private final SemanticSimilarityService semanticSimilarityService;

    private final FactorSimilarityService factorSimilarityService;

    @Override
    @Transactional
    public boolean initNewVector() {
        Long questionaryId = questionaryRepository.getPublishedQuestionaryWithUninitializedVectorWithLock();
        if (questionaryId == null) {
            return false;
        }
        Questionary questionary = questionaryRepository.findById(questionaryId).orElseThrow();

        Long questionaryTypeId = questionary.getType().getId();
        QuestionaryType type = questionaryTypeCache.getTypeModel(questionaryTypeId);
        Map<Long, Question> questionMap = type.getQuestionBlocks().stream()
                .flatMap(b -> b.getQuestions().stream()).collect(Collectors.toMap(Question::getId, v -> v));

        fillSemanticVector(questionary, questionMap);
        fillFactorVector(questionary, questionMap);
        questionary.setVectorsInitialized(true);

        questionaryRepository.save(questionary);
        return true;
    }

    private void fillSemanticVector(Questionary questionary, Map<Long, Question> questionMap) {
        try {
            List<SemanticSimilarityAnswerAnalyzeTask> tasks = new ArrayList<>();
            for (Answer answer : questionary.getAnswers()) {
                String textAnswer = answer.getTextValue();
                if (textAnswer == null) {
                    continue;
                }
                Question question = questionMap.get(answer.getQuestion().getId());
                if (!question.getForSemanticSimilarity()) {
                    continue;
                }
                SemanticSimilarityAnswerAnalyzeTask task = new SemanticSimilarityAnswerAnalyzeTask(textAnswer, question.getWeightCoefficientForVector());
                tasks.add(task);
            }

            if (tasks.isEmpty()) {
                questionary.setSemanticVector(new double[Constants.SEMANTIC_VECTOR_SIZE]);
                return;
            }

            double[] semanticVector = semanticSimilarityService.analyze(tasks);
            questionary.setSemanticVector(semanticVector);
        } catch (Exception e) {
            log.error("Ошибка при обновлении семантического вектора", e);
        }
    }

    private void fillFactorVector(Questionary questionary, Map<Long, Question> questionMap) {
        try {
            List<FactorSimilarityAnalyzeTask> tasks = new ArrayList<>();
            for (Answer answer : questionary.getAnswers()) {
                if (answer.getChosenOptions() == null) {
                    continue;
                }
                Question question = questionMap.get(answer.getQuestion().getId());
                Float questionWeight = question.getWeightCoefficientForVector();
                Map<Integer, Float> factorTotalInfluenceMap = new HashMap<>();
                for (String chosenOption : answer.getChosenOptions()) {
                    AnswerOption option = question.getAnswerOptions().stream()
                            .filter(o -> Objects.equals(o.getKey(), chosenOption))
                            .findFirst().orElseThrow();

                    for (int factorIndex = 0; factorIndex < option.getInfluence().length; factorIndex++) {
                        factorTotalInfluenceMap.putIfAbsent(factorIndex, 0f);
                        factorTotalInfluenceMap.put(factorIndex, factorTotalInfluenceMap.get(factorIndex) + option.getInfluence()[factorIndex]);
                    }
                }

                for (Map.Entry<Integer, Float> entry : factorTotalInfluenceMap.entrySet()) {
                    Integer factorIndex = entry.getKey();
                    Float totalInfluence = entry.getValue();
                    tasks.add(new FactorSimilarityAnalyzeTask(factorIndex, totalInfluence, questionWeight));
                }
            }

            if (tasks.isEmpty()) {
                questionary.setFactorVector(new double[Constants.FACTOR_VECTOR_SIZE]);
                return;
            }

            double[] factorVector = factorSimilarityService.analyze(tasks);
            questionary.setFactorVector(factorVector);
        } catch (Exception e) {
            log.error("Ошибка при обновлении факторного вектора", e);
        }
    }
}
