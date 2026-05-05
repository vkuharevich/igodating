package com.igodating.service.vector.impl;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import com.igodating.constant.Constants;
import com.igodating.service.vector.FactorSimilarityAnalyzeTask;
import com.igodating.service.vector.FactorSimilarityService;
import com.igodating.service.vector.WhiteningMatrixService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FactorSimilarityServiceImpl implements FactorSimilarityService {

    private static final double ALPHA = 1.0;

    private final WhiteningMatrixService whiteningMatrixService;

    @Override
    public double[] analyze(List<FactorSimilarityAnalyzeTask> tasks) {
        double[] result = new double[Constants.FACTOR_VECTOR_SIZE];

        for (FactorSimilarityAnalyzeTask task : tasks) {
            result[task.index()] += task.weight() * task.influence();
        }

        return postProcessVector(result);
    }

    public double[] postProcessVector(double[] vector) {
        try (NDManager manager = NDManager.newBaseManager()) {
            NDArray postProcessed = manager.create(vector);
            postProcessed = postProcessed.mul(ALPHA);
            postProcessed = postProcessed.tanh();

            return whiteningMatrixService.applyWhitening(postProcessed.toDoubleArray());
        }
    }
}
