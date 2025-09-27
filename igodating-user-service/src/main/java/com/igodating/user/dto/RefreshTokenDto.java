package com.igodating.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RefreshTokenDto {
    private final UUID token;
    private final Instant refreshExpiration;
}
