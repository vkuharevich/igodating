package com.igodating.user.dto;

import com.igodating.commons.security.JwtUserWrapper;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final JwtUserWrapper principal;

    public JwtAuthenticationToken(JwtUserWrapper principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.setAuthenticated(true);
    }


    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}

