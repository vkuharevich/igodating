package com.igodating.questionary.dto.textembedding;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TextEmbeddingResponseItem(
        @JsonProperty("sentence_id")
        String sentenceId,
        float[] embedding
) {
}
