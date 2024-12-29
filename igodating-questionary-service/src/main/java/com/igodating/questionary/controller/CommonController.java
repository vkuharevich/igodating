package com.igodating.questionary.controller;

import com.igodating.questionary.model.constant.QuestionAnswerType;
import com.igodating.questionary.model.constant.RuleAccessType;
import com.igodating.questionary.model.constant.RuleMatchingType;
import com.igodating.questionary.model.constant.UserQuestionaryStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
@Slf4j
public class CommonController {

    @GetMapping("/answer-types")
    public ResponseEntity<QuestionAnswerType[]> answerTypes() {
        return ResponseEntity.ok(QuestionAnswerType.values());
    }

    @GetMapping("/rule-access-types")
    public ResponseEntity<RuleAccessType[]> ruleAccessTypes() {
        return ResponseEntity.ok(RuleAccessType.values());
    }

    @GetMapping("/rule-matching-types")
    public ResponseEntity<RuleMatchingType[]> ruleMatchingTypes() {
        return ResponseEntity.ok(RuleMatchingType.values());
    }

    @GetMapping("/user-questionary-statuses")
    public ResponseEntity<UserQuestionaryStatus[]> userQuestionaryStatuses() {
        return ResponseEntity.ok(UserQuestionaryStatus.values());
    }
}
