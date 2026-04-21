package com.igodating.service.vector.impl;

import ai.djl.Application;
import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import com.igodating.constant.Constants;
import com.igodating.service.vector.SemanticSimilarityAnswerAnalyzeTask;
import com.igodating.service.vector.SemanticSimilarityService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Log4j2
@Service
public class SemanticSimilarityServiceImpl implements SemanticSimilarityService {

    @Value("${model.path}")
    private String modelPath;

    @Value("${model.url}")
    private String modelUrl;

    private ZooModel<String, float[]> model;

    private Predictor<String, float[]> predictor;

    @PostConstruct
    public void init() throws ModelNotFoundException, MalformedModelException, IOException {
        try {
            Criteria<String, float[]> criteria = Criteria.builder()
                    .setTypes(String.class, float[].class)
                    .optApplication(Application.NLP.TEXT_EMBEDDING)
                    .optModelPath(Path.of(modelPath))
                    .optModelUrls(modelUrl)
                    .build();

            if (!criteria.isDownloaded()) {
                criteria.downloadModel();
            }

            model = criteria.loadModel();
            predictor = model.newPredictor();
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    @Override
    public double[] analyze(List<SemanticSimilarityAnswerAnalyzeTask> tasks) throws TranslateException {
        List<float[]> predictResults = predictor.batchPredict(tasks.stream().map(SemanticSimilarityAnswerAnalyzeTask::text).toList());

        double[] vector = new double[Constants.SEMANTIC_VECTOR_SIZE];
        for (int index = 0; index < tasks.size(); index++) {
            SemanticSimilarityAnswerAnalyzeTask task = tasks.get(index);
            float coefficient = task.weight();
            float[] resultWithWeightCoefficient = new float[Constants.SEMANTIC_VECTOR_SIZE];
            for (int j = 0; j < predictResults.get(index).length; j++) {
                resultWithWeightCoefficient[j] = coefficient * predictResults.get(index)[j];
            }

            for (int i = 0; i < resultWithWeightCoefficient.length; i++) {
                vector[i] += resultWithWeightCoefficient[i];
            }
        }

        return postProcessVector(vector);
    }

    @PreDestroy
    public void destroy() {
        model.close();
    }

    private double[] postProcessVector(double[] vector) {
        try (NDManager manager = NDManager.newBaseManager()) {
            NDArray postProcessed  = manager.create(vector);
            NDArray norm = postProcessed.norm();
            postProcessed = postProcessed.div(norm);

            return postProcessed.toDoubleArray();
        }
    }
}
