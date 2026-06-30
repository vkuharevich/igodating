package com.igodating.commons.security.filters;

import com.igodating.commons.security.models.JwtAuthenticationToken;
import com.igodating.commons.security.models.JwtBackendUser;
import com.igodating.commons.security.JwtConstants;
import com.igodating.commons.security.models.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
public class JwtBackendAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final JwtParser jwtParser;
    private JsonMapper mapper;

    public JwtBackendAuthenticationFilter(SecretKey secretKey, JsonMapper mapper) {
        this(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), secretKey, mapper);
        this.mapper = mapper;
    }

    public JwtBackendAuthenticationFilter(AuthenticationEntryPoint authenticationEntryPoint, SecretKey secretKey, JsonMapper mapper) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.mapper = mapper;
        this.jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(JwtConstants.HEADER_BACKEND_AUTHORIZATION);
        if (!StringUtils.startsWithIgnoreCase(header, "bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        final String token = header.substring(7);
        try {
            final Claims body = this.jwtParser
                    .parseSignedClaims(token)
                    .getPayload();
            final Set<SimpleGrantedAuthority> authorities = Arrays.stream(body.get(JwtConstants.AUTHORITIES, String.class)
                    .split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());

            JwtUser jwtUser = body.get(JwtConstants.USER, String.class) != null ? mapper.readValue(body.get(JwtConstants.USER, String.class), JwtUser.class) : null;
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(new JwtBackendUser(authorities, jwtUser, token), authorities);

            authentication.setDetails(authenticationDetailsSource.buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (ExpiredJwtException | IllegalArgumentException | SignatureException | MalformedJwtException |
                 UnsupportedJwtException e) {
            if (!(e instanceof ExpiredJwtException)) {
                log.error(e);
            }
            authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("Invalid Backend token", e));
        }
    }
}
