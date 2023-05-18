package com.tan.springboot2.p7;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class TokenController {
    @Autowired
    private TokenService tokenService;

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        String token = tokenService.generateToken();
        tokenService.setTokenCookie(response, token);
        return "Login successful!";
    }

    @GetMapping("/secure")
    public String secure(HttpServletRequest request) {
        if (tokenService.verifyToken(request)) {
            return "Access granted!";
        } else {
            return "Access denied!";
        }
    }
}
