package com.igodating.geodata.dto;

import com.igodating.geodata.model.constans.CountryCode;

public record CountryUpdateRequest(
        Long id,
        CountryCode code,
        String name
) {
}
