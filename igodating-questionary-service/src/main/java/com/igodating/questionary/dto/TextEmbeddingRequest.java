package com.igodating.questionary.dto;

import java.util.List;

public record TextEmbeddingRequest(
        List<TextEmbeddingRequestItem> sentences
) {
}
