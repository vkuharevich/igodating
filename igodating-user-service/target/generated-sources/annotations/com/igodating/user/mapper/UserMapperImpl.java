package com.igodating.user.mapper;

import com.igodating.commons.security.JwtUser;
import com.igodating.commons.security.PrivilegeDto;
import com.igodating.commons.security.RoleDto;
import com.igodating.user.dto.UserDto;
import com.igodating.user.dto.request.UserCreateRequest;
import com.igodating.user.entity.PrivilegeEntity;
import com.igodating.user.entity.RoleEntity;
import com.igodating.user.entity.UserEntity;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-27T14:40:53+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21 (BellSoft)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public JwtUser mapToJwtUser(UserEntity user) {
        if ( user == null ) {
            return null;
        }

        JwtUser.JwtUserBuilder jwtUser = JwtUser.builder();

        jwtUser.id( user.getId() );
        jwtUser.firstName( user.getFirstName() );
        jwtUser.secondName( user.getSecondName() );
        jwtUser.middleName( user.getMiddleName() );
        jwtUser.birthday( user.getBirthday() );
        jwtUser.phone( user.getPhone() );
        jwtUser.email( user.getEmail() );
        jwtUser.password( user.getPassword() );
        jwtUser.blocked( user.isBlocked() );
        jwtUser.roles( roleEntitySetToRoleDtoSet( user.getRoles() ) );
        jwtUser.createdAt( user.getCreatedAt() );
        jwtUser.deletedAt( user.getDeletedAt() );

        return jwtUser.build();
    }

    @Override
    public UserDto mapUserToDto(UserEntity user) {
        if ( user == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setId( user.getId() );
        userDto.setFirstName( user.getFirstName() );
        userDto.setSecondName( user.getSecondName() );
        userDto.setMiddleName( user.getMiddleName() );
        userDto.setBirthday( user.getBirthday() );
        userDto.setPhone( user.getPhone() );
        userDto.setEmail( user.getEmail() );
        userDto.setBlocked( user.isBlocked() );
        Set<RoleEntity> set = user.getRoles();
        if ( set != null ) {
            userDto.setRoles( new LinkedHashSet<RoleEntity>( set ) );
        }
        userDto.setCreatedAt( user.getCreatedAt() );
        userDto.setDeletedAt( user.getDeletedAt() );

        return userDto;
    }

    @Override
    public UserEntity mapCreateRequestToUserEntity(UserCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setFirstName( request.getFirstName() );
        userEntity.setSecondName( request.getSecondName() );
        userEntity.setMiddleName( request.getMiddleName() );
        userEntity.setBirthday( request.getBirthday() );
        userEntity.setPhone( request.getPhone() );
        userEntity.setEmail( request.getEmail() );
        userEntity.setPassword( request.getPassword() );

        return userEntity;
    }

    protected PrivilegeDto privilegeEntityToPrivilegeDto(PrivilegeEntity privilegeEntity) {
        if ( privilegeEntity == null ) {
            return null;
        }

        PrivilegeDto.PrivilegeDtoBuilder privilegeDto = PrivilegeDto.builder();

        privilegeDto.id( privilegeEntity.getId() );
        privilegeDto.name( privilegeEntity.getName() );
        privilegeDto.description( privilegeEntity.getDescription() );

        return privilegeDto.build();
    }

    protected Set<PrivilegeDto> privilegeEntitySetToPrivilegeDtoSet(Set<PrivilegeEntity> set) {
        if ( set == null ) {
            return null;
        }

        Set<PrivilegeDto> set1 = LinkedHashSet.newLinkedHashSet( set.size() );
        for ( PrivilegeEntity privilegeEntity : set ) {
            set1.add( privilegeEntityToPrivilegeDto( privilegeEntity ) );
        }

        return set1;
    }

    protected RoleDto roleEntityToRoleDto(RoleEntity roleEntity) {
        if ( roleEntity == null ) {
            return null;
        }

        RoleDto.RoleDtoBuilder roleDto = RoleDto.builder();

        roleDto.id( roleEntity.getId() );
        roleDto.name( roleEntity.getName() );
        roleDto.description( roleEntity.getDescription() );
        roleDto.privilegeEntities( privilegeEntitySetToPrivilegeDtoSet( roleEntity.getPrivilegeEntities() ) );

        return roleDto.build();
    }

    protected Set<RoleDto> roleEntitySetToRoleDtoSet(Set<RoleEntity> set) {
        if ( set == null ) {
            return null;
        }

        Set<RoleDto> set1 = LinkedHashSet.newLinkedHashSet( set.size() );
        for ( RoleEntity roleEntity : set ) {
            set1.add( roleEntityToRoleDto( roleEntity ) );
        }

        return set1;
    }
}
