package com.malykhnik.jwttokenrefreshtoken.repository;

import com.malykhnik.jwttokenrefreshtoken.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
}
