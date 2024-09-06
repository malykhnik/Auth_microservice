package com.malykhnik.authorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class JwtTokenRefreshTokenApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtTokenRefreshTokenApplication.class, args);
    }

}
