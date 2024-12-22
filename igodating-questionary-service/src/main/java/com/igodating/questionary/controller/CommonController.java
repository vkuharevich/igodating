package com.igodating.questionary.controller;

import com.igodating.questionary.model.constant.QuestionAnswerType;
import com.igodating.questionary.model.constant.RuleAccessType;
import com.igodating.questionary.model.constant.RuleMatchingType;
import com.igodating.questionary.model.constant.UserQuestionaryStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CommonController {

    @QueryMapping
//    @Secured("ROLE_VIEW_QUESTIONARY_TEMPLATE")
    public QuestionAnswerType[] answerTypes() {
        return QuestionAnswerType.values();
    }

    @QueryMapping
    public RuleAccessType[] ruleAccessTypes() {
        return RuleAccessType.values();
    }

    @QueryMapping
    public RuleMatchingType[] ruleMatchingTypes() {
        return RuleMatchingType.values();
    }

    @QueryMapping
    public UserQuestionaryStatus[] userQuestionaryStatuses() {
        return UserQuestionaryStatus.values();
    }
}
