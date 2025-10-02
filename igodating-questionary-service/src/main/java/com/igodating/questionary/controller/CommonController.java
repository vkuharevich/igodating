package com.igodating.questionary.controller;

import com.igodating.questionary.model.constant.QuestionAnswerType;
import com.igodating.questionary.model.constant.RuleAccessType;
import com.igodating.questionary.model.constant.RuleMatchingType;
import com.igodating.questionary.model.constant.UserQuestionaryStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Общий контроллер", description = "Возвращает общие ENUM")
public class CommonController {

    @Operation(summary = "Типы ответов", description = "Возвращает все типы ответов в анкете")
    @GetMapping("/answer-types")
    public ResponseEntity<QuestionAnswerType[]> answerTypes() {
        return ResponseEntity.ok(QuestionAnswerType.values());
    }

    @Operation(summary = "Типы доступа для правила", description = "Возвращает все типы доступа правила подбора")
    @GetMapping("/rule-access-types")
    public ResponseEntity<RuleAccessType[]> ruleAccessTypes() {
        return ResponseEntity.ok(RuleAccessType.values());
    }

    @Operation(summary = "Операции подбора", description = "Возвращает все операции подбора")
    @GetMapping("/rule-matching-types")
    public ResponseEntity<RuleMatchingType[]> ruleMatchingTypes() {
        return ResponseEntity.ok(RuleMatchingType.values());
    }

    @Operation(summary = "Статусы анкеты", description = "Возвращает все возможные статусы по анкете")
    @GetMapping("/user-questionary-statuses")
    public ResponseEntity<UserQuestionaryStatus[]> userQuestionaryStatuses() {
        return ResponseEntity.ok(UserQuestionaryStatus.values());
    }
}
