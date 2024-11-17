package com.igodating.questionary.dto;

import java.util.List;

public record CursorResponse<T>(
        List<T> content,
        Long cursor,
        Integer size
) {
}
