package com.igodating.questionary.util.tsquery.impl;

import com.igodating.questionary.dto.filter.FullTextSearchSettings;
import com.igodating.questionary.util.tsquery.TsQueryConverter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class TsQueryConverterImpl implements TsQueryConverter {

    private static final String AND_OPERATOR = " & ";

    @Override
    public String fullTextSearchSettingsToTsQuery(FullTextSearchSettings fullTextSearchSettings) {
        return String.join(AND_OPERATOR, fullTextSearchSettings.keywords());
    }

    @Override
    public FullTextSearchSettings tsQueryToFullTextSearchSettings(String tsQuery) {
        return new FullTextSearchSettings(Arrays.stream(tsQuery.split(AND_OPERATOR)).toList());
    }
}
