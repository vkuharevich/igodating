package com.igodating.geodata.dto;

import com.igodating.geodata.model.constans.CountryCode;

public record CountryCreateRequest(
        CountryCode code,
        String name
) {
}
