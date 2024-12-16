package com.igodating.questionary.dto.userquestionary;

public record UserQuestionaryRecommendation(
        Long id,
        String userId,
        Double similarity
) {
}
