package com.igodating.commons.security.filters;

import com.igodating.commons.security.JwtConstants;
import com.igodating.commons.security.models.JwtAuthenticationToken;
import com.igodating.commons.security.models.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;

import javax.crypto.SecretKey;
import java.io.IOException;


public class JwtUserAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
    private final JsonMapper mapper;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final Http403ForbiddenEntryPoint http403ForbiddenEntryPoint = new Http403ForbiddenEntryPoint();
//    private PublicKey publicKey;
    private SecretKey secretKey;
    private RequestMatcher requestMatcher;

    public JwtUserAuthenticationFilter(SecretKey secretKey, AuthenticationEntryPoint authenticationEntryPoint, JsonMapper mapper) {
        this.secretKey = secretKey;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.mapper = mapper;
    }

    protected String extractAuthorizationHeader(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (this.authenticationIsRequired() && StringUtils.startsWithIgnoreCase(header, "bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String tokenValue = this.extractAuthorizationHeader(request);
        if (tokenValue == null) {
            chain.doFilter(request, response);
            return;
        }
        try {
            try {
                final Claims claims = Jwts.parser()
                        .verifyWith(this.secretKey)
                        .clockSkewSeconds(45L)
                        .build()
                        .parseSignedClaims(tokenValue)
                        .getPayload();
                final boolean isAuthorized = this.authorizeUser(request, claims);
                if (isAuthorized) {
                    chain.doFilter(request, response);
                } else {
                    http403ForbiddenEntryPoint.commence(request, response, null);
                }
            } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | IncorrectClaimException | SignatureException e) {
                throw new BadCredentialsException(e instanceof IncorrectClaimException ? "User platform/browser was changed" : "Invalid JWT signature", e);
            } catch (ExpiredJwtException e) {
                throw new BadCredentialsException("Expired JWT", e);
            }
        } catch (AuthenticationException e) {
            logger.error(e);
            authenticationEntryPoint.commence(request, response, e);
        }
    }

    private boolean authorizeUser(HttpServletRequest request, Claims body) throws IOException {
        final JwtUser user = this.mapper.readValue(body.get(JwtConstants.USER, String.class), JwtUser.class);
        if (user.isBlocked()) {
            return false;
        }
        final JwtAuthenticationToken authentication = new JwtAuthenticationToken(user, user.getAuthorities());
        authentication.setDetails(authenticationDetailsSource.buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return true;
    }


    protected boolean authenticationIsRequired() {
        Authentication existingAuth = SecurityContextHolder.getContext()
                .getAuthentication();

        if (existingAuth == null || !existingAuth.isAuthenticated()) {
            return true;
        }
        return existingAuth instanceof AnonymousAuthenticationToken;
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (this.requestMatcher != null) {
            return !requestMatcher.matches(request);
        }
        return false;
    }


}
