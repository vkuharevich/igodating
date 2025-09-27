package com.igodating.commons.security;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Entity
@AllArgsConstructor
public class JwtBackendUser implements UserDetails, JwtUserWrapper{

    public static final String BACKEND_USERNAME = "backend";

    private final Set<SimpleGrantedAuthority> authorities;

    private final JwtUser jwtUser;

    private final String token;

    @Override
    public JwtUser getJwtUser() {
        return jwtUser;
    }

    @Override
    public boolean isBackendWrapper() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.token;
    }

    @Override
    public String getUsername() {
        return BACKEND_USERNAME;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
