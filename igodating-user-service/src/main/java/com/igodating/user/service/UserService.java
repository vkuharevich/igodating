package com.igodating.user.service;

import com.igodating.commons.security.JwtConstants;
import com.igodating.commons.security.JwtUser;
import com.igodating.commons.security.UserAuthenticateResponse;
import com.igodating.user.dto.JwtAuthenticationToken;
import com.igodating.user.dto.RefreshTokenDto;
import com.igodating.user.dto.UserAuthenticationDto;
import com.igodating.user.dto.UserDto;
import com.igodating.user.dto.request.UserAuthenticationRequest;
import com.igodating.user.dto.request.UserCreateRequest;
import com.igodating.user.entity.UserEntity;
import com.igodating.user.exception.RefreshTokenExpiredException;
import com.igodating.user.mapper.UserMapper;
import com.igodating.user.repository.UserRepository;
import com.igodating.user.util.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
@AllArgsConstructor
public class UserService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JwtUtils jwtUtils;
    UserMapper userMapper;

    @Transactional
    public UserAuthenticateResponse generateToken(UserAuthenticationRequest request) {
        log.debug("Request for token generation are given {}", request);
        UserEntity authenticatedUser = this.getUserEntityForAuthentication(request);
        return this.createJwt(authenticatedUser);
    }

    public UserDto getUserById(long userId) {
        log.debug("Request for getting user {} are given", userId);
        final UserEntity userEntity = this.userRepository.findById(userId);
        return this.userMapper.mapUserToDto(userEntity);
    }

    @Transactional
    public long createUser(UserCreateRequest request) {
        log.debug("Request for user creation are given {}", request);
        final String encodedPassword = passwordEncoder.encode(request.getPassword());
        final UserEntity userEntity = this.userMapper.mapCreateRequestToUserEntity(request);
        userEntity.setPassword(encodedPassword);
        return this.userRepository.save(userEntity).getId();
    }

//    @Transactional
//    public UserAuthenticateResponse refreshToken(String token) {
//        final Claims claims = this.jwtUtils.parseJwt(token);
//
//        final Instant now = Instant.now();
//        final Instant refreshExpiration = Instant.ofEpochSecond(claims.get(JwtConstants.REFRESH_LIFETIME, Long.class));
//        if (now.compareTo(refreshExpiration) > 0) throw new RefreshTokenExpiredException();
//
//        final UUID refreshToken = UUID.fromString(claims.get(JwtConstants.REFRESH_TOKEN, String.class));
//
//
//        final Instant newRefreshExpiration = now.plus(this.jwtSigner.getRefreshLife());
//
//        final boolean tokenUpdated = this.authRepository.updateJwtToken(new RefreshTokenDto(refreshToken, newRefreshExpiration), now);
//        if (!tokenUpdated) {
//            throw new RefreshTokenExpiredException();
//        }
//        final UserDto result = this.getUserById(Long.parseLong(claims.getSubject()));
//        final JwtUser old = this.jwtSigner.extractOldToken(claims);
//        if (result.isBlocked() || result.isAccountBlocked(old.getCurrentProfile())) {
//            throw new LockedException("User name or password invalid");
//        }
//        final JwtUser newSecurityUser = this.mapUser(result, old.getCurrentProfile(), old.isMobileUser());
//        this.fillSecurityContext(newSecurityUser);
//        return new UserAuthenticateResponse(this.jwtSigner.updateJwt(newSecurityUser, new RefreshTokenDto(refreshToken, refreshExpiration)), newSecurityUser.getPrivileges());
//    }

    private UserEntity getUserEntityForAuthentication(UserAuthenticationRequest request){
        final Optional<UserAuthenticationDto> authenticationDto = userRepository.findAuthenticationDto(request.getLogin());
        final UserAuthenticationDto userAuthenticationDto = authenticationDto.orElseThrow(() -> new RuntimeException("not found user by email"));
        if (!this.passwordEncoder.matches(request.getPassword(), userAuthenticationDto.getPassword())) {
            throw new RuntimeException("password not match");
        }
        return userRepository.findById(userAuthenticationDto.getId());
    }

    private UserAuthenticateResponse createJwt(UserEntity user) {
        final JwtUser jwtSecurityUser = this.userMapper.mapToJwtUser(user);
        final JwtUtils.JwtCreateResponse jwt = this.jwtUtils.createJwt(jwtSecurityUser);
        this.fillSecurityContext(jwtSecurityUser);
        return new UserAuthenticateResponse(user.getId(), jwt.getJwt(), jwtSecurityUser.getPrivileges());
    }

    private void fillSecurityContext(JwtUser jwtUser) {
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwtUser, jwtUser.getAuthorities()));
    }
}
