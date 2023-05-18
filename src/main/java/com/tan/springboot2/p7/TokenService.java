package com.tan.springboot2.p7;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {
    private static final String TOKEN_NAME = "token";
    private static final long TOKEN_EXPIRATION = 3600; // Token过期时间，单位为秒

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String generateToken() {
        String token = UUID.randomUUID().toString();
        String redisKey = getTokenRedisKey(token);
        // 存储token到Redis，并设置过期时间
        redisTemplate.opsForValue().set(redisKey, token, TOKEN_EXPIRATION, TimeUnit.SECONDS);
        return token;
    }

    public boolean verifyToken(HttpServletRequest request) {
        String token = getTokenFromCookie(request);
        if (StringUtils.hasText(token)) {
            String redisKey = getTokenRedisKey(token);
            // 检查Redis中是否存在该token
            return redisTemplate.hasKey(redisKey);
        }
        return false;
    }

    public void setTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(TOKEN_NAME, token);
        cookie.setMaxAge((int) TOKEN_EXPIRATION);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(TOKEN_NAME)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getTokenRedisKey(String token) {
        return "token:" + token;
    }
}
