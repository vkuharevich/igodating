package com.igodating.user.controller;

import com.igodating.commons.security.UserAuthenticateResponse;
import com.igodating.user.dto.request.UserAuthenticationRequest;
import com.igodating.user.service.UserService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@RequestMapping("/api/users")
public class AuthController {

    UserService userService;

    @PostMapping("/generateToken")
    public ResponseEntity<UserAuthenticateResponse> generateJwt(@RequestBody UserAuthenticationRequest request) {
        return ResponseEntity.ok(userService.generateToken(request));
    }
}
