package com.igodating.user.dto;

import com.igodating.user.entity.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {

    private Long id;
    private String firstName;
    private String secondName;
    private String middleName;
    private LocalDateTime birthday;
    private String phone;
    private String email;
    private boolean blocked;
    private Set<RoleEntity> roles = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}
