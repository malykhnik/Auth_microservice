package com.malykhnik.jwttokenrefreshtoken.controller;

import com.malykhnik.jwttokenrefreshtoken.dto.JwtResponseDto;
import com.malykhnik.jwttokenrefreshtoken.dto.RefreshTokenRequestDto;
import com.malykhnik.jwttokenrefreshtoken.dto.SignInDto;
import com.malykhnik.jwttokenrefreshtoken.dto.SignUpDto;
import com.malykhnik.jwttokenrefreshtoken.entity.RefreshToken;
import com.malykhnik.jwttokenrefreshtoken.entity.User;
import com.malykhnik.jwttokenrefreshtoken.service.RedisTokenBlackList;
import com.malykhnik.jwttokenrefreshtoken.service.JwtService;
import com.malykhnik.jwttokenrefreshtoken.service.RefreshTokenService;
import com.malykhnik.jwttokenrefreshtoken.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class AuthController {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final RedisTokenBlackList tokenBlacklist;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> authenticateAndGetToken(@RequestBody SignInDto signInDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInDto.getUsername(), signInDto.getPassword()));
        if (authentication.isAuthenticated()) {
            String accessToken = jwtService.generateToken(signInDto.getUsername());
            String refreshToken = refreshTokenService.createOrUpdateRefreshToken(signInDto.getUsername()).getToken();
            return ResponseEntity.ok().body(JwtResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build());
        } else {
            throw new UsernameNotFoundException("invalid user request..!!");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            tokenBlacklist.addToBlacklist(token);
        }

        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody SignUpDto signUpDto) {
        return ResponseEntity.ok().body(userService.save(signUpDto));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<JwtResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDTO) {
        return ResponseEntity.ok().body(refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(userInfo -> {
                    String accessToken = jwtService.generateToken(userInfo.getUsername());
                    return JwtResponseDto.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshTokenRequestDTO.getToken()).build();
                }).orElseThrow(() -> new RuntimeException("Refresh Token is not in DB..!!")));
    }

}
