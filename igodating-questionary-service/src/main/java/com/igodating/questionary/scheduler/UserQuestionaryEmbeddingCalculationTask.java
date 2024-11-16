package com.igodating.questionary.scheduler;

public interface UserQuestionaryEmbeddingCalculationTask {

    void executeEmbeddingCalculation(int batchSize, int threadsCount);
}
