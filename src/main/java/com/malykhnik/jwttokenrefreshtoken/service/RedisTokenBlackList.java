package com.malykhnik.jwttokenrefreshtoken.service;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
@Service
public class RedisTokenBlackList {
    private static final String BLACKLIST_CACHE = "blacklist";

    // Метод добавляет токен в черный список (кэш)
    @CachePut(value = BLACKLIST_CACHE, key = "#token")
    public String addToBlacklist(String token) {
        // Возвращаемое значение будет сохранено в кэше с ключом, равным значению переменной "token".
        return token;
    }

    // Если токен есть в кэше, вернется сам токен, иначе вернется null
    @Cacheable(value = BLACKLIST_CACHE, key = "#token")
    public String isBlacklisted(String token) {
        System.out.println("Cacheable выполнился");
        return null;
    }
}