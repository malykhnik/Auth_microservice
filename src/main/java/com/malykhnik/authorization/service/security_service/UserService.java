package com.malykhnik.authorization.service.security_service;

import com.malykhnik.authorization.dto.security_dto.SignUpDto;
import com.malykhnik.authorization.entity.security_entity.Role;
import com.malykhnik.authorization.entity.security_entity.User;
import com.malykhnik.authorization.repository.security_repository.RoleRepo;
import com.malykhnik.authorization.repository.security_repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder encoder;

    public User save(SignUpDto signUpDto) {
        Optional<User> userOptional = userRepo.findByUsername(signUpDto.getUsername());
        if (userOptional.isEmpty()) {
            Optional<Role> roleOptional = roleRepo.findByName(signUpDto.getRole());
            Role role;

            if (roleOptional.isPresent()) {
                role = roleOptional.get();
            } else {
                role = Role.builder()
                        .name(signUpDto.getRole())
                        .build();
                roleRepo.save(role);
            }

            User user = User.builder()
                    .username(signUpDto.getUsername())
                    .password(encoder.encode(signUpDto.getPassword()))
                    .role(role)
                    .build();
            userRepo.save(user);
            return user;

        } else {
            throw new RuntimeException("Такой юзер уже есть!");
        }
    }
}
