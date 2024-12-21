package com.igodating.questionary.util.tsquery.impl;

import com.igodating.questionary.constant.CommonConstants;
import com.igodating.questionary.util.tsquery.TsQueryConverter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class TsQueryConverterImpl implements TsQueryConverter {

    private static final String AND_OPERATOR = " & ";

    @Override
    public String strToTsQuery(String value) {
        return String.join(AND_OPERATOR, value.split(CommonConstants.VALUE_SPLITTER));
    }

    @Override
    public String tsQueryToStr(String tsQuery) {
        return Arrays.stream(tsQuery.split(AND_OPERATOR)).collect(Collectors.joining(CommonConstants.VALUE_SPLITTER));
    }
}
