package com.igodating.user.controller;

import com.igodating.commons.dto.ResponseWrapper;
import com.igodating.user.dto.UserDto;
import com.igodating.user.dto.request.UserCreateRequest;
import com.igodating.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import liquibase.license.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Tag(description = "Users API", name = "Users")
public class UserController {

    UserService userService;

    @GetMapping(UserRoutes.USER_BY_ID)
    public ResponseWrapper<UserDto> getUser(@PathVariable("id") Long id) {
        return ResponseWrapper.ok(userService.getUserById(id));
    }

    @PostMapping(UserRoutes.USERS)
    public ResponseWrapper<Long> createUser(@RequestBody UserCreateRequest request) {
        return ResponseWrapper.ok(userService.createUser(request));
    }
}
