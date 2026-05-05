package com.igodating.service;

import com.igodating.dto.questionarytype.view.RecommendationView;
import com.igodating.dto.recommendations.request.RecommendationsRequest;
import org.springframework.data.domain.Slice;

public interface RecommendationService {

    Slice<RecommendationView> getRecommendations(RecommendationsRequest request);
}
