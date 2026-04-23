package com.igodating.service.vector;

import ai.djl.translate.TranslateException;

import java.util.List;

public interface SemanticSimilarityService {

    double[] analyze(List<SemanticSimilarityAnswerAnalyzeTask> tasks) throws TranslateException;
}
