package com.igodating.questionary.model.view;

public record UserQuestionaryRecommendationView(
        Long id,
        String userId,
        Double similarity
) {
}
