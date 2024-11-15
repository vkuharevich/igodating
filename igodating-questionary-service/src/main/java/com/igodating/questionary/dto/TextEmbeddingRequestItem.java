package com.igodating.questionary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TextEmbeddingRequestItem(
        @JsonProperty("sentence_id")
        String sentenceId,
        String value
) {
}
