package com.malykhnik.jwttokenrefreshtoken.service;

import com.malykhnik.jwttokenrefreshtoken.entity.RefreshToken;
import com.malykhnik.jwttokenrefreshtoken.entity.User;
import com.malykhnik.jwttokenrefreshtoken.repository.RefreshTokenRepository;
import com.malykhnik.jwttokenrefreshtoken.repository.UserRepo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    public String createOrUpdateRefreshToken(String username) {
        Optional<User> userOptional = userRepo.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Проверяем, есть ли уже RefreshToken для данного пользователя
            Optional<RefreshToken> refreshTokenOptional = refreshTokenRepo.findByUser(user);
            RefreshToken refreshToken;

            if (refreshTokenOptional.isPresent()) {
                // Если токен уже существует, обновляем его
                refreshToken = refreshTokenOptional.get();
                refreshTokenRepo.save(refreshToken);
                return refreshToken.getToken();
            } else {
                // Если токена нет, создаем новый
                refreshToken = RefreshToken.builder()
                        .user(user)
                        .token(UUID.randomUUID().toString())
                        .expiryDate(Instant.now().plusMillis(1000 * 60 * 30)) // 30 мин
                        .build();
                refreshTokenRepo.save(refreshToken);
                return refreshToken.getToken();
            }
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