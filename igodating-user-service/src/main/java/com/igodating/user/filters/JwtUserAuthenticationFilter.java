package com.igodating.user.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.PublicKey;


public class JwtUserAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
    private final ObjectMapper mapper;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final Http403ForbiddenEntryPoint http403ForbiddenEntryPoint = new Http403ForbiddenEntryPoint();
    @Setter
    //todo reset public key
    private PublicKey publicKey;
    private RequestMatcher requestMatcher;

    public JwtUserAuthenticationFilter(PublicKey publicKey, ObjectMapper mapper) {
        this.publicKey = publicKey;
        this.mapper = mapper;
        this.authenticationEntryPoint = new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }

    public JwtUserAuthenticationFilter(PublicKey publicKey, AuthenticationEntryPoint authenticationEntryPoint, ObjectMapper mapper) {
        this.publicKey = publicKey;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.mapper = mapper;
    }

    public JwtUserAuthenticationFilter(PublicKey publicKey, AuthenticationEntryPoint authenticationEntryPoint, ObjectMapper mapper,
                                       RequestMatcher requestMatcher) {
        this.publicKey = publicKey;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.mapper = mapper;
        this.requestMatcher = requestMatcher;
    }

//    protected String extractAuthorizationHeader(HttpServletRequest request) {
//        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
//        if (this.authenticationIsRequired() && StringUtils.startsWithIgnoreCase(header, "bearer ")) {
//            return header.substring(7);
//        }
//        return null;
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
//        String tokenValue = this.extractAuthorizationHeader(request);
//        if (tokenValue == null) {
//            chain.doFilter(request, response);
//            return;
//        }
//        try {
//            try {
//                final Claims claims = Jwts.parser()
//                        .verifyWith(this.publicKey)
//                        .clockSkewSeconds(45L)
//                        .build()
//                        .parseSignedClaims(tokenValue)
//                        .getPayload();
//                final boolean isAuthorized = this.authorizeUser(request, claims);
//                if (isAuthorized) {
//                    chain.doFilter(request, response);
//                } else {
//                    http403ForbiddenEntryPoint.commence(request, response, null);
//                }
//            } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | IncorrectClaimException | SignatureException e) {
//                throw new BadCredentialsException(e instanceof IncorrectClaimException ? "User platform/browser was changed" : "Invalid JWT signature", e);
//            } catch (ExpiredJwtException e) {
//                throw new BadCredentialsException("Expired JWT", e);
//            }
//        } catch (AuthenticationException e) {
//            logger.error(e);
//            authenticationEntryPoint.commence(request, response, e);
//        }
    }

//    private boolean authorizeUser(HttpServletRequest request, Claims body) throws IOException {
//        final JwtUser user = this.mapper.readValue(body.get(JwtConstants.USER, String.class), JwtUser.class);
//        if (user.isBlocked()) {
//            return false;
//        }
//        final JwtAuthenticationToken authentication = new JwtAuthenticationToken(user, user.getAuthorities());
//        authentication.setDetails(authenticationDetailsSource.buildDetails(request));
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        return true;
//    }
//
//
//    protected boolean authenticationIsRequired() {
//        Authentication existingAuth = SecurityContextHolder.getContext()
//                .getAuthentication();
//
//        if (existingAuth == null || !existingAuth.isAuthenticated()) {
//            return true;
//        }
//        return existingAuth instanceof AnonymousAuthenticationToken;
//    }
//
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        if (this.requestMatcher != null) {
//            return !requestMatcher.matches(request);
//        }
//        return false;
//    }


}
