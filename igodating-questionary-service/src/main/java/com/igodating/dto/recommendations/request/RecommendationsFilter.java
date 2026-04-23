package com.igodating.dto.recommendations.request;

public record RecommendationsFilter(
        Long filterId,
        String[] optionValues,
        Float valueFrom,
        Float valueTo
) {
}
