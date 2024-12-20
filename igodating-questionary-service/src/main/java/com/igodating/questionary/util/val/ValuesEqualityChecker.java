package com.igodating.questionary.util.val;

import com.igodating.questionary.model.Question;

public interface ValuesEqualityChecker {

    boolean equals(String firstVal, String secondVal, Question question);
}
