package com.igodating.dto.questionarytype.view;

public record RecommendationView(
    Long questionaryId,
    String questionaryName,
    Long userId,
    Double score
) {
}
