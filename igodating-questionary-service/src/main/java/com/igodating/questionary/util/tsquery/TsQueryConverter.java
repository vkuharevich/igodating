package com.igodating.questionary.util.tsquery;

import com.igodating.questionary.dto.filter.FullTextSearchSettings;

public interface TsQueryConverter {

    String fullTextSearchSettingsToTsQuery(FullTextSearchSettings fullTextSearchSettings);

    FullTextSearchSettings tsQueryToFullTextSearchSettings(String tsQuery);
}
