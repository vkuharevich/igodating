package com.igodating.questionary.dto.textembedding;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TextEmbeddingRequestItem(
        @JsonProperty("sentence_id")
        String sentenceId,
        String value
) {
}
