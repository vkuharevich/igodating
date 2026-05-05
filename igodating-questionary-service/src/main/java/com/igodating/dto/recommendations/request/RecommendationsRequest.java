package com.igodating.dto.recommendations.request;

import java.util.List;

public record RecommendationsRequest(
        Long questionaryId,
        List<RecommendationsFilter> filters,
        int limit,
        int offset
) {
}
