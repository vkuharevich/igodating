package com.igodating.commons.security;

import com.igodating.commons.security.models.JwtUserWrapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import javax.crypto.SecretKey;

public class BackendHeaderThreadLocalServletExchangeFilterFunction implements ExchangeFilterFunction {

    private final JsonMapper jsonMapper;
    private final String appName;
    private final SecretKey secretKey;

    public BackendHeaderThreadLocalServletExchangeFilterFunction(JsonMapper jsonMapper, String appName, SecretKey secretKey) {
        this.jsonMapper = jsonMapper;
        this.appName = appName;
        this.secretKey = secretKey;
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest clientRequest, ExchangeFunction nextFilter) {

        return Mono.deferContextual(contextView -> {
            final JwtUserWrapper user;
            if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof JwtUserWrapper userWrapper) {
                user = userWrapper.getJwtUser();
            } else {
                user = contextView.getOrDefault(JwtUserWrapper.class, null);
            }
            if (user != null) {
                JwtBuilder jwtBuilder;
                jwtBuilder = Jwts.builder()
                        .issuer(appName)
                        .claim(JwtConstants.AUTHORITIES, JwtConstants.BACKEND_AUTHORITY)
                        .claim(JwtConstants.USER, jsonMapper.writeValueAsString(user.getJwtUser()));
                String token = "Bearer " + jwtBuilder.signWith(secretKey).compact();
                return nextFilter.exchange(ClientRequest.from(clientRequest)
                        .headers(httpHeaders -> httpHeaders.set(JwtConstants.HEADER_BACKEND_AUTHORIZATION, token))
                        .build());
            }
            return nextFilter.exchange(clientRequest);
        });
    }
}
