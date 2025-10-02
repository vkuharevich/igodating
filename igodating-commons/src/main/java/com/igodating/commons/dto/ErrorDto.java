package com.igodating.commons.dto;

import java.time.LocalDateTime;

public record ErrorDto(
        String errorMessage,
        LocalDateTime dateTime
) {

}
