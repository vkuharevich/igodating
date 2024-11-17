package com.igodating.questionary.dto.userquestionary;

import com.igodating.questionary.model.constant.UserQuestionaryStatus;

import java.time.LocalDateTime;
import java.util.List;

public record UserQuestionaryView(
        Long id,
        String title,
        String description,
        String userId,
        Long questionaryTemplateId,
        UserQuestionaryStatus questionaryStatus,
        LocalDateTime createdAt,
        LocalDateTime deletedAt,
        List<UserQuestionaryAnswerView> answers
) {
}
