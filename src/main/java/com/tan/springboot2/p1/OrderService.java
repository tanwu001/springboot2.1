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

    private final RedissonClient redissonClient;

    @Autowired
    public OrderService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public boolean takeOrder(Integer id) {
        RLock lock = redissonClient.getLock(LOCK_KEY + id);
        try {
            // 尝试获取分布式锁，设置锁的过期时间，避免锁长时间占用
            boolean locked = lock.tryLock(10, 600, TimeUnit.SECONDS);
            if (locked) {
                log.info("cg");
                // 在这里实现接单逻辑
                Thread.sleep(580 * 1000L);
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

