package com.igodating.geodata.dto;

public record CityCreateRequest(
        String name,
        Long regionId
) {
}
