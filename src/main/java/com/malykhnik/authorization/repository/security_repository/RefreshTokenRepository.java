package com.malykhnik.authorization.repository.security_repository;

import com.malykhnik.authorization.entity.security_entity.RefreshToken;
import com.malykhnik.authorization.entity.security_entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
}
