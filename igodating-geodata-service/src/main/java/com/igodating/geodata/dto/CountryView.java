package com.igodating.geodata.dto;

import com.igodating.geodata.model.constans.CountryCode;

import java.time.LocalDateTime;

public record CountryView(
        Long id,
        CountryCode countryCode,
        String name,
        LocalDateTime createdAt,
        LocalDateTime deletedAt
) {
}
