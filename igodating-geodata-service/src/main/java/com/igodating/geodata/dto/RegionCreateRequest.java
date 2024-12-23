package com.igodating.geodata.dto;

public record RegionCreateRequest(
        String name,
        Long countryId
) {
}
