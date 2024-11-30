package com.igodating.questionary.dto.filter;

import java.util.List;

public record FullTextSearchSettings(
        List<String> keywords
) {
}
