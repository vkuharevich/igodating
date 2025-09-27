package com.igodating.commons.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleDto {

    private Long id;
    private Role name;
    private String description;
    private Set<PrivilegeDto> privilegeEntities;
}
