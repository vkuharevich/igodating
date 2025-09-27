package com.igodating.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCreateRequest {

    @NotBlank
    private String firstName;
    @NotBlank
    private String secondName;
    private String middleName;
    private LocalDateTime birthday;
    @NotNull
    private String phone;
    @NotNull
    private String email;
    @NotNull
    private String password;
}
