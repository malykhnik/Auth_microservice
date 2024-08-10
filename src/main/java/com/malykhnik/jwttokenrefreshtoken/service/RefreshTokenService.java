package com.malykhnik.jwttokenrefreshtoken.service;

import com.malykhnik.jwttokenrefreshtoken.entity.RefreshToken;
import com.malykhnik.jwttokenrefreshtoken.entity.User;
import com.malykhnik.jwttokenrefreshtoken.repository.RefreshTokenRepository;
import com.malykhnik.jwttokenrefreshtoken.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepo;

    private final UserRepo userRepo;

    public RefreshToken createOrUpdateRefreshToken(String username) {
        Optional<User> userOptional = userRepo.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Проверяем, есть ли уже RefreshToken для данного пользователя
            Optional<RefreshToken> existingTokenOptional = refreshTokenRepo.findByUser(user);
            RefreshToken refreshToken;

            if (existingTokenOptional.isPresent()) {
                // Если токен уже существует, обновляем его
                refreshToken = existingTokenOptional.get();
                refreshToken.setToken(UUID.randomUUID().toString());
                refreshToken.setExpiryDate(Instant.now().plusMillis(1000 * 60 * 30)); // 30 мин
            } else {
                // Если токена нет, создаем новый
                refreshToken = RefreshToken.builder()
                        .user(user)
                        .token(UUID.randomUUID().toString())
                        .expiryDate(Instant.now().plusMillis(1000 * 60 * 30)) // 30 мин
                        .build();
            }

            return refreshTokenRepo.save(refreshToken);
        } else {
            throw new UsernameNotFoundException("Такого юзера нет!");
        }
    }


    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepo.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepo.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }

}