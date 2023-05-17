package com.tan.springboot2.p1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/delivery")
public class DeliveryController2 {
    private static final String DELIVERY_ORDER_ACCEPTED_KEY = "delivery_order_accepted";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/publish")
    public String publishDeliveryOrder(String orderId) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        // 尝试获取锁
        boolean lockAcquired = ops.setIfAbsent(DELIVERY_ORDER_ACCEPTED_KEY, orderId);

        if (lockAcquired) {
            // 成功获取到锁，表示骑手接到了单子
            // 这里可以保存配送单的详细信息到Redis中，使用Hash结构等

            // 返回接单成功的消息
            return "骑手已接到单子：" + orderId;
        } else {
            // 无法获取锁，表示已经有其他骑手接到了单子
            // 返回接单失败的消息
            return "该单子已被其他骑手接走";
        }
    }
}
