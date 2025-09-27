package com.igodating.user.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;

@Log4j2
public class JwtBackendAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final JwtParser jwtParser;
    private ObjectMapper objectMapper;

    public JwtBackendAuthenticationFilter(SecretKey secretKey, ObjectMapper objectMapper) {
        this(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), secretKey, objectMapper);
        this.objectMapper = objectMapper;
    }

    public JwtBackendAuthenticationFilter(AuthenticationEntryPoint authenticationEntryPoint, SecretKey secretKey, ObjectMapper objectMapper) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.objectMapper = objectMapper;
        this.jwtParser = Jwts.parser()
                .verifyWith(secretKey)
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
//        String header = request.getHeader(JwtConstants.HEADER_BACKEND_AUTHORIZATION);
//        if (!StringUtils.startsWithIgnoreCase(header, "bearer ")) {
//            chain.doFilter(request, response);
//            return;
//        }
//        final String token = header.substring(7);
//        try {
//            final Claims body = this.jwtParser
//                    .parseSignedClaims(token)
//                    .getPayload();
//            final Set<SimpleGrantedAuthority> authorities = Arrays.stream(body.get(JwtConstants.AUTHORITIES, String.class)
//                    .split(","))
//                    .map(SimpleGrantedAuthority::new)
//                    .collect(Collectors.toSet());
//
//            JwtSecurityUser jwtSecurityUser = body.get(JwtConstants.USER, String.class) != null ? objectMapper.readValue(body.get(JwtConstants.USER, String.class), JwtSecurityUser.class) : null;
//            JwtAuthenticationToken authentication = new JwtAuthenticationToken(new JwtBackendUser(token, authorities, jwtSecurityUser), authorities);
//
//            authentication.setDetails(authenticationDetailsSource.buildDetails(request));
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            chain.doFilter(request, response);
//        } catch (ExpiredJwtException | IllegalArgumentException | SignatureException | MalformedJwtException | UnsupportedJwtException e) {
//            if (!(e instanceof ExpiredJwtException)) {
//                log.error(e);
//            }
//            authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("Invalid Backend token", e));
//        }
    }
}
