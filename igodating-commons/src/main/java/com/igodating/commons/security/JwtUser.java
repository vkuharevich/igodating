package com.igodating.commons.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtUser implements UserDetails, JwtUserWrapper {

    private Long id;
    private String firstName;
    private String secondName;
    private String middleName;
    private LocalDateTime birthday;
    private String phone;
    private String email;
    private String password;
    private boolean blocked;
    private Set<RoleDto> roles;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    @Override
    @JsonIgnore
    public JwtUser getJwtUser() {
        return this;
    }

    @Override
    public boolean isBackendWrapper() {
        return false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (blocked) return Collections.emptySet();
        return this.roles.stream()
                .flatMap(x -> Stream.concat(Stream.of(x.getName().name()), x.getPrivilegeEntities().stream().map(priv->priv.getName().name())))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    public EnumSet<Privilege> getPrivileges() {
        if (blocked) return EnumSet.noneOf(Privilege.class);
        return this.roles.stream()
                .flatMap(x->x.getPrivilegeEntities().stream())
                .map(PrivilegeDto::getName)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Privilege.class)));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }
}
