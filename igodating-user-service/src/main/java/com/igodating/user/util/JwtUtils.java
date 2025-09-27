package com.igodating.user.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igodating.commons.security.JwtConstants;
import com.igodating.commons.security.JwtUser;
import com.igodating.user.config.JwtKeysProperties;
import com.igodating.user.dto.RefreshTokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtUtils {

    private final Duration accessLife;
    @Getter
    private final Duration refreshLife;
    private final ObjectMapper mapper;
    private final String basicKey;


    public JwtUtils(JwtKeysProperties keysProperties, ObjectMapper mapper) {

        this.mapper = mapper;
        this.accessLife = keysProperties.getAccessLifetime();
        this.refreshLife = keysProperties.getRefreshLifetime();
        this.basicKey = keysProperties.getKeys().getBasicKey();
    }

    @SneakyThrows
    public JwtUser extractOldToken(Claims claims) {
        return mapper.readValue(claims.get(JwtConstants.USER, String.class), JwtUser.class);
    }


    @SneakyThrows
    public String updateJwt(JwtUser user,
                            RefreshTokenDto refreshToken) {
        return this.create(user, refreshToken);
    }

    public JwtCreateResponse createJwt(JwtUser user) {
        final RefreshTokenDto refreshToken = new RefreshTokenDto(UUID.randomUUID(), Instant.now().plus(this.refreshLife));
        return new JwtCreateResponse(this.create(user, refreshToken), refreshToken);
    }

    @SneakyThrows
    private String create(JwtUser user, RefreshTokenDto refreshToken) {

        final Instant now = Instant.now();
        Instant expirationAccess = now.plus(this.accessLife);
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expirationAccess))
                .claim(JwtConstants.REFRESH_TOKEN, refreshToken.getToken().toString())
                .claim(JwtConstants.REFRESH_LIFETIME, refreshToken.getRefreshExpiration().getEpochSecond())
                .claim(JwtConstants.USER, mapper.writeValueAsString(user))
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(basicKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Извлечение данных из токена
     *
     * @param token           токен
     * @param claimsResolvers функция извлечения данных
     * @param <T>             тип данных
     * @return данные
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    /**
     * Извлечение всех данных из токена
     *
     * @param token токен
     * @return данные
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    @AllArgsConstructor
    @Getter
    public static class JwtCreateResponse {
        private final String jwt;
        private final RefreshTokenDto refreshToken;
    }
}
