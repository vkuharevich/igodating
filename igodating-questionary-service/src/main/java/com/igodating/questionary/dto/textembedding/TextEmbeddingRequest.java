package com.igodating.questionary.dto.textembedding;

import java.util.List;

public record TextEmbeddingRequest(
        List<TextEmbeddingRequestItem> sentences
) {
}
