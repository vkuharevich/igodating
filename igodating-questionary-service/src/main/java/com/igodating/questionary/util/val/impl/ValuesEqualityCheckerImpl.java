package com.igodating.questionary.util.val.impl;

import com.igodating.questionary.constant.CommonConstants;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.constant.QuestionAnswerType;
import com.igodating.questionary.util.val.ValuesEqualityChecker;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ValuesEqualityCheckerImpl implements ValuesEqualityChecker {

    @Override
    public boolean equals(String firstVal, String secondVal, Question question) {
        if (Objects.equals(firstVal, secondVal)) {
            return true;
        }

        if (QuestionAnswerType.MULTIPLE_CHOICE.equals(question.getAnswerType())) {
            Set<String> firstSet = Arrays.stream(firstVal.split(CommonConstants.VALUE_SPLITTER)).collect(Collectors.toSet());
            Set<String> secondSet = Arrays.stream(secondVal.split(CommonConstants.VALUE_SPLITTER)).collect(Collectors.toSet());

            return firstSet.equals(secondSet);
        }

        return false;
    }
}
