package com.tan.springboot2.p2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/delivery")
public class DeliveryController {
    private static final String DELIVERY_ORDER_ACCEPTED_KEY_PREFIX = "delivery_order_accepted:";
    private static final long LOCK_EXPIRATION_SECONDS = 300;
    private static final long LOCK_RENEWAL_SECONDS = 10;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/publish")
    public String publishDeliveryOrder(String orderId) {
        String key = DELIVERY_ORDER_ACCEPTED_KEY_PREFIX + orderId;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String value = UUID.randomUUID().toString();
        boolean lockAcquired=false;
        try {
            // 尝试获取锁
             lockAcquired = ops.setIfAbsent(key, value, LOCK_EXPIRATION_SECONDS, TimeUnit.SECONDS);
            if (lockAcquired) {
                // 成功获取到锁，表示骑手接到了单子
                // 这里可以保存配送单的详细信息到Redis中，使用Hash结构等
                log.info("cg");
                Thread.sleep(280*1000L);
                // 返回接单成功的消息
                return "骑手已接到单子：" + orderId;
            } else {
                log.info("sb");
                // 返回接单失败的消息
                return "该单子已被其他骑手接走" + orderId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (lockAcquired) {
                releaseLock(key, value);
            }
        }
        return key;
    }

    private void releaseLock(String key, String value) {
        RedisScript<Long> script = new DefaultRedisScript<>(
                "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",
                Long.class
        );
        redisTemplate.execute(script, Collections.singletonList(key), value);
    }


}
