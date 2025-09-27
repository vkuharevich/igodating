package com.igodating.user.mapper;

import com.igodating.commons.security.JwtUser;
import com.igodating.user.dto.UserDto;
import com.igodating.user.dto.request.UserCreateRequest;
import com.igodating.user.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    JwtUser mapToJwtUser(UserEntity user);

    UserDto mapUserToDto(UserEntity user);

    UserEntity mapCreateRequestToUserEntity(UserCreateRequest request);
}
