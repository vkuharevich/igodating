package com.igodating.geodata.dto;

import java.time.LocalDateTime;

public record RegionView(
        Long id,
        String name,
        Long countryId,
        CountryView country,
        LocalDateTime createdAt,
        LocalDateTime deletedAt
) {
}
