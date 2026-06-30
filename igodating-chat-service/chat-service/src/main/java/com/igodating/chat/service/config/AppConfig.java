package com.igodating.chat.service.config;

import com.igodating.commons.security.JwtBackendProperties;
import com.igodating.commons.security.JwtConstants;
import com.igodating.user.connector.UserServiceConnector;
import com.igodating.commons.security.BackendHeaderThreadLocalServletExchangeFilterFunction;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.DeferringLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@EnableScheduling
@EnableMethodSecurity
public class AppConfig {

    @Bean
    public BackendHeaderThreadLocalServletExchangeFilterFunction backendHeaderFilterFunction(JsonMapper objectMapper,
                                                                                             @Value("${spring.application.name}") String appName,
                                                                                             JwtBackendProperties jwtBackendProperties) {
        return new BackendHeaderThreadLocalServletExchangeFilterFunction(objectMapper, appName, Keys.hmacShaKeyFor(jwtBackendProperties.getBack2BackKey().getBytes()));
    }

    @Bean
    public WebClient loadBalancedWebClient(DeferringLoadBalancerExchangeFilterFunction<?> function,
                                           BackendHeaderThreadLocalServletExchangeFilterFunction filterFunction,
                                           @Value("${spring.application.name}") String appName, WebClient.Builder builder,
                                           JwtBackendProperties jwtBackendProperties) {
        return builder
                .filter(function)
                .filter(filterFunction)
                .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().maxInMemorySize(Math.toIntExact(DataSize.ofMegabytes(2).toBytes())))
                .defaultHeader(JwtConstants.HEADER_BACKEND_AUTHORIZATION, "Bearer " + Jwts.builder()
                        .issuer(appName)
                        .claim(JwtConstants.AUTHORITIES, JwtConstants.BACKEND_AUTHORITY)
                        .signWith(Keys.hmacShaKeyFor(jwtBackendProperties.getBack2BackKey().getBytes()))
                        .compact())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public UserServiceConnector userServiceConnector(WebClient loadBalancedWebClient, JsonMapper mapper) {
        return new UserServiceConnector(loadBalancedWebClient, mapper);
    }
}
