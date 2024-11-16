package com.igodating.questionary.service.validation;

import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.Question;

public interface MatchingRuleValidationService {

    void validateOnCreate(MatchingRule matchingRule, Question question);

    void validateOnUpdate(MatchingRule matchingRule, Question question);
}
