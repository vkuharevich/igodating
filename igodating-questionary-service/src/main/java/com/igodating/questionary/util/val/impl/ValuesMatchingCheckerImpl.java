package com.igodating.questionary.util.val.impl;

import com.igodating.questionary.constant.CommonConstants;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.constant.RuleMatchingType;
import com.igodating.questionary.util.val.ValuesMatchingChecker;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ValuesMatchingCheckerImpl implements ValuesMatchingChecker {

    @Override
    public boolean match(String when, String then, Question question) {
        if (Objects.equals(when, then)) {
            return true;
        }

        if (question.withChoice()) {
            Set<String> firstSet = Arrays.stream(when.split(CommonConstants.VALUE_SPLITTER)).collect(Collectors.toSet());
            Set<String> secondSet = Arrays.stream(then.split(CommonConstants.VALUE_SPLITTER)).collect(Collectors.toSet());

            return firstSet.containsAll(secondSet);
        }

        if (RuleMatchingType.IN_RANGE.equals(question.getMatchingRule().getMatchingType())) {
            if (then.contains(CommonConstants.VALUE_SPLITTER)) {
                Double[] firstRange = Arrays.stream(when.split(CommonConstants.VALUE_SPLITTER)).map(Double::parseDouble).toArray(Double[]::new);
                Double[] secondRange = Arrays.stream(then.split(CommonConstants.VALUE_SPLITTER)).map(Double::parseDouble).toArray(Double[]::new);

                return checkRangesOverlap(firstRange, secondRange);
            }

            Double[] range = Arrays.stream(when.split(CommonConstants.VALUE_SPLITTER)).map(Double::parseDouble).toArray(Double[]::new);
            Double num = Double.parseDouble(then);

            return checkInRange(range, num);
        }

        return false;
    }

    private boolean checkInRange(Double[] range, Double num) {
        Double low = range[0];
        Double up = range[1];

        return low <= num && up >= num;
    }

    private boolean checkRangesOverlap(Double[] firstRange, Double[] secondRange) {
        Double firstLow = firstRange[0];
        Double firstUp = firstRange[1];

        Double secondLow = secondRange[0];
        Double secondUp = secondRange[1];

        return Math.min(firstUp, secondUp) - Math.max(firstLow, secondLow) >= 0;
    }
}
