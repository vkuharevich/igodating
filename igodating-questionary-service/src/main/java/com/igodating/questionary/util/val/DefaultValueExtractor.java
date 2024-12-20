package com.igodating.questionary.util.val;

import com.igodating.questionary.model.MatchingRuleDefaultValues;
import com.igodating.questionary.model.Question;

public interface DefaultValueExtractor {

    String extractDefaultValueForMatchingByAnswer(String answer, Question question);
}
