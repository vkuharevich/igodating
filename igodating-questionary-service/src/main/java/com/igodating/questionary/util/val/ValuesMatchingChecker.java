package com.igodating.questionary.util.val;

import com.igodating.questionary.model.Question;

public interface ValuesMatchingChecker {

    boolean match(String firstVal, String secondVal, Question question);
}
