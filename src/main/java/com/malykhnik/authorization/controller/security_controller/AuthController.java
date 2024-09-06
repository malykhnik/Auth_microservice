package com.malykhnik.authorization.controller.security_controller;

import com.malykhnik.authorization.dto.security_dto.JwtResponseDto;
import com.malykhnik.authorization.dto.security_dto.RefreshTokenRequestDto;
import com.malykhnik.authorization.dto.security_dto.SignInDto;
import com.malykhnik.authorization.dto.security_dto.SignUpDto;
import com.malykhnik.authorization.entity.security_entity.RefreshToken;
import com.malykhnik.authorization.entity.security_entity.User;
import com.malykhnik.authorization.service.security_service.RedisTokenBlackList;
import com.malykhnik.authorization.service.security_service.JwtService;
import com.malykhnik.authorization.service.security_service.RefreshTokenService;
import com.malykhnik.authorization.service.security_service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
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
            String refreshToken = refreshTokenService.createOrUpdateRefreshToken(signInDto.getUsername());
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
        System.out.println(refreshTokenRequestDTO.toString());
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

    @GetMapping("/getCurrentRole")
    public ResponseEntity<String> getCurrentRole() {
        List<String> roles = new ArrayList<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            for (GrantedAuthority authority : auth.getAuthorities()) {
                roles.add(authority.getAuthority());
            }
        }
        if (auth != null) {
            System.out.println("Authentication: " + auth);
            System.out.println("Authorities: " + auth.getAuthorities());
        }
        System.out.println(roles.toString());
        return ResponseEntity.ok().body(roles.get(0));
    }
}
