package com.igodating.geodata.dto;

public record CityUpdateRequest(
        Long id,
        String name,
        Long regionId
) {
}
