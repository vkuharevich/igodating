package com.igodating.geodata.dto;

import java.time.LocalDateTime;

public record CityView(
        Long id,
        String name,
        Long regionId,
        RegionView region,
        LocalDateTime createdAt,
        LocalDateTime deletedAt
) {
}
