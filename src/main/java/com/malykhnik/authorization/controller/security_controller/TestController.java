package com.malykhnik.authorization.controller.security_controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class TestController {
    @GetMapping("/test")
    public String test() {
        return "Успешный доступ!";
    }
}
