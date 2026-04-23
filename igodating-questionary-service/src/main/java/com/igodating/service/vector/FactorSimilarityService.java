package com.igodating.service.vector;

import java.util.List;

public interface FactorSimilarityService {

    double[] analyze(List<FactorSimilarityAnalyzeTask> tasks);
}
