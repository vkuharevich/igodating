package com.igodating.commons.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.EnumSet;

@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserAuthenticateResponse {

    private Long userId;
    private String token;
    private EnumSet<Privilege> privileges;
}
