package com.tan.springboot2.p4;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsVerificationCodeService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String SMS_SENDTIME = "sms:sendtime:";
    private static final String SMS_COUNT = "sms:count:";
    private static final String SMS_CODE = "sms:code:";


    /**
     * 发送短信验证码
     *
     * @param mobile 手机号码
     * @return true：发送成功；false：发送失败
     */
    public boolean sendVerificationCode(String mobile) {
        // 1. 判断是否能够发送验证码
        String lastSendTimeStr = redisTemplate.opsForValue().get(SMS_SENDTIME + mobile);
        if (lastSendTimeStr != null) {
            long lastSendTime = Long.parseLong(lastSendTimeStr);
            if (System.currentTimeMillis() - lastSendTime < 60 * 1000) {
                // 距离上次发送时间不足1分钟，不能发送验证码
                return false;
            }
        }

        // 2. 判断该手机号码是否超过发送次数限制
        String countStr = redisTemplate.opsForValue().get(SMS_COUNT + mobile);
        if (countStr != null && Integer.parseInt(countStr) >= 3) {
            // 今天已经发送了3次，不能发送验证码
            return false;
        }

        // 3. 生成验证码并存储
        String verificationCode = generateRandomCode(6);
        redisTemplate.opsForValue().set(SMS_CODE + mobile, verificationCode, 5, TimeUnit.MINUTES);
        // 更新发送时间和发送次数
        if (lastSendTimeStr == null) {
            redisTemplate.opsForValue().set(SMS_SENDTIME + mobile, String.valueOf(System.currentTimeMillis()));
        } else {
            redisTemplate.opsForValue().set(SMS_SENDTIME + mobile, String.valueOf(System.currentTimeMillis()), 1, TimeUnit.MINUTES);
        }
        if (countStr == null) {
            redisTemplate.opsForValue().set(SMS_COUNT + mobile, "1", 1, TimeUnit.DAYS);
        } else {
            redisTemplate.opsForValue().increment(SMS_COUNT + mobile);
        }

        // 4. TODO：发送短信验证码

        return true;
    }

    /**
     * 验证短信验证码
     *
     * @param mobile           手机号码
     * @param verificationCode 验证码
     * @return true：验证成功；false：验证失败
     */
    public boolean verifyVerificationCode(String mobile, String verificationCode) {
        String code = redisTemplate.opsForValue().get(SMS_CODE + mobile);
        if (code != null && code.equals(verificationCode)) {
            redisTemplate.delete(SMS_CODE + mobile);
            return true;
        }
        return false;
    }

    /**
     * 生成短信验证码
     *
     * @return 验证码
     */
    private static String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10); // 生成0到9之间的随机数
            sb.append(digit);
        }
        log.info("验证码："+sb.toString());
        return sb.toString();
    }

}