package com.igodating.geodata.dto;

public record RegionUpdateRequest(
        Long id,
        String name,
        Long countryId
) {
}
