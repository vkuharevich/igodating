package com.igodating.user.repository;

import com.igodating.user.dto.UserAuthenticationDto;
import com.igodating.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query(value = """
            SELECT u
            FROM UserEntity u
            WHERE u.email = :email and not u.blocked and u.deletedAt is null
            """)
    Optional<UserAuthenticationDto> findAuthenticationDto(String email);

    UserEntity findById(long id);
}
