package com.igodating.questionary.util.val.impl;

import com.igodating.questionary.constant.CommonConstants;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.constant.RuleMatchingType;
import com.igodating.questionary.util.val.ValuesMatchingChecker;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ValuesMatchingCheckerImpl implements ValuesMatchingChecker {

    @Override
    public boolean match(String firstVal, String secondVal, Question question) {
        if (Objects.equals(firstVal, secondVal)) {
            return true;
        }

        if (question.withChoice()) {
            Set<String> firstSet = Arrays.stream(firstVal.split(CommonConstants.VALUE_SPLITTER)).collect(Collectors.toSet());
            Set<String> secondSet = Arrays.stream(secondVal.split(CommonConstants.VALUE_SPLITTER)).collect(Collectors.toSet());

            return firstSet.containsAll(secondSet);
        }

        if (RuleMatchingType.IN_RANGE.equals(question.getMatchingRule().getMatchingType())) {
            Double[] firstRange = Arrays.stream(firstVal.split(CommonConstants.VALUE_SPLITTER)).map(Double::parseDouble).toArray(Double[]::new);
            Double[] secondRange = Arrays.stream(secondVal.split(CommonConstants.VALUE_SPLITTER)).map(Double::parseDouble).toArray(Double[]::new);

            return checkRangesOverlap(firstRange, secondRange);
        }

        return false;
    }

    private boolean checkRangesOverlap(Double[] firstRange, Double[] secondRange) {
        Double firstLow = firstRange[0];
        Double firstUp = firstRange[1];

        Double secondLow = secondRange[0];
        Double secondUp = secondRange[1];

        return Math.min(firstUp, secondUp) - Math.max(firstLow, secondLow) >= 0;
    }
}
