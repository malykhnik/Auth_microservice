package com.malykhnik.jwttokenrefreshtoken.repository.securityRepository;

import com.malykhnik.jwttokenrefreshtoken.entity.securityEntity.RefreshToken;
import com.malykhnik.jwttokenrefreshtoken.entity.securityEntity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
}
