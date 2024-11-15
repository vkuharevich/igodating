package com.igodating.questionary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TextEmbeddingResponse(
    List<TextEmbeddingResponseItem> sentences,
    @JsonProperty("global_embedding")
    float[] globalEmbedding
) {
}
