package com.igodating.config;

import com.igodating.commons.security.Http401AuthenticationEntryPoint;
import com.igodating.commons.security.JwtConstants;
import com.igodating.commons.security.filters.JwtBackendAuthenticationFilter;
import com.igodating.commons.security.filters.JwtUserAuthenticationFilter;
import com.igodating.commons.security.models.Privilege;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.json.JsonMapper;

import javax.crypto.SecretKey;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final SecretKey secretKey;

    public SecurityConfig(JwtKeysProperties jwtKeysProperties) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtKeysProperties.getKeys().getSecretKey());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public JwtUserAuthenticationFilter jwtUserAuthenticationFilter(JsonMapper mapper) {
        return new JwtUserAuthenticationFilter(this.secretKey,
                new Http401AuthenticationEntryPoint(),
                mapper
        );
    }

    @Bean
    public JwtBackendAuthenticationFilter jwtBackendAuthenticationFilter(JsonMapper mapper) {
        return new JwtBackendAuthenticationFilter(new Http401AuthenticationEntryPoint(), this.secretKey, mapper);
    }

    @Configuration(proxyBeanMethods = false)
    static class MainSecurityConfig {
        private final JwtUserAuthenticationFilter jwtUserAuthenticationFilter;
        private final JwtBackendAuthenticationFilter jwtBackendAuthenticationFilter;
        private final boolean swaggerEnabled;

        MainSecurityConfig(@Qualifier("jwtUserAuthenticationFilter") JwtUserAuthenticationFilter jwtUserAuthenticationFilter,
                           JwtBackendAuthenticationFilter jwtBackendAuthenticationFilter,
                           @Value("${springdoc.api-docs.enabled:false}") boolean swaggerEnabled) {
            this.jwtUserAuthenticationFilter = jwtUserAuthenticationFilter;
            this.jwtBackendAuthenticationFilter = jwtBackendAuthenticationFilter;
            this.swaggerEnabled = swaggerEnabled;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, WebProperties webProperties) throws Exception {
            var config = http.authorizeHttpRequests(req ->
                    req.requestMatchers(webProperties.getError().getPath()).permitAll()
                            .requestMatchers(HttpMethod.POST, "api/v1/questionary")
                            .hasAnyAuthority(Privilege.WRITE_QUESTIONARY.name(), JwtConstants.BACKEND_AUTHORITY)
                            .requestMatchers(HttpMethod.POST, "api/v1/questionary/answers")
                            .hasAnyAuthority(Privilege.WRITE_QUESTIONARY.name(), JwtConstants.BACKEND_AUTHORITY)
                            .requestMatchers(HttpMethod.POST, "api/v1/questionary/publication/")
                            .hasAnyAuthority(Privilege.WRITE_QUESTIONARY.name(), JwtConstants.BACKEND_AUTHORITY)
                            .requestMatchers(HttpMethod.POST, "/api/v1/questionary-type/")
                            .hasAnyAuthority(Privilege.WRITE_QUESTIONARY.name(), JwtConstants.BACKEND_AUTHORITY)
                            .anyRequest().permitAll());
            config
                    .headers(AbstractHttpConfigurer::disable)
                    .sessionManagement(x -> x.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .logout(AbstractHttpConfigurer::disable)
                    .csrf(AbstractHttpConfigurer::disable)
                    .addFilterAt(jwtUserAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(jwtBackendAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            if (swaggerEnabled) {
                http.httpBasic(Customizer.withDefaults());
            }
            return http.build();
        }
    }
}
