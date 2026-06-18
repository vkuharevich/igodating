package com.igodating.user.controller;

import com.igodating.commons.dto.ResponseWrapper;
import com.igodating.commons.security.models.UserAuthenticateResponse;
import com.igodating.user.dto.request.UserAuthenticationRequest;
import com.igodating.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@RequestMapping("/api/users")
@Tag(description = "Authentication API", name = "Authentication")
public class AuthController {

    UserService userService;

    @PostMapping("/generateToken")
    @Operation(summary = "JWT token generation", description = "User authentication by login and password")
    public ResponseWrapper<UserAuthenticateResponse> generateJwt(@RequestBody UserAuthenticationRequest request) {
        return ResponseWrapper.ok(userService.generateToken(request));
    }
}
