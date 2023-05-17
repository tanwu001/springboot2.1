package com.tan.springboot2.p1;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OrderService {

    private static final String LOCK_KEY = "order-lock:";

    @Autowired
    private RedissonClient redissonClient;

    public boolean takeOrder(Integer orderId) {
        RLock lock = redissonClient.getLock(LOCK_KEY + orderId);
        try {
            // 尝试获取分布式锁，设置锁的过期时间，避免锁长时间占用
            boolean locked = lock.tryLock(10, 300, TimeUnit.SECONDS);
            if (locked) {
                log.info("cg");
                // 在这里实现接单逻辑
                Thread.sleep(280 * 1000L);
                // ...
                return true; // 接单成功
            } else {
                log.info("sb");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock(); // 释放锁
            }
        }
        return false; // 接单失败
    }
}

