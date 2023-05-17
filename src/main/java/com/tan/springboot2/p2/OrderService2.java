package com.tan.springboot2.p2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
public class OrderService2 {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public boolean grabOrder(String orderId, String riderId) {
        String lockKey = "order_lock_" + orderId;
        String lockValue = UUID.randomUUID().toString();
        // 设置分布式锁，过期时间为10秒
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, Duration.ofSeconds(10));
        if (result != null && result) {
            // 成功获取锁，执行业务逻辑
            try {
                // ...处理订单
                return true;
            } finally {
                // 释放锁
                String value = redisTemplate.opsForValue().get(lockKey);
                if (lockValue.equals(value)) {
                    redisTemplate.delete(lockKey);
                }
                //redisTemplate.execute();

            }
        } else {
            // 获取锁失败，返回false
            return false;
        }
    }
}