package com.tan.springboot2.p3;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class DistributedLockInterceptor {
    @Autowired
    private  RedissonClient redissonClient;

    @Around("@annotation(DistributedLock)")
    public Object intercept(ProceedingJoinPoint joinPoint) throws Throwable {
        //要在@Around注解中访问HttpServletRequest对象，你可以通过RequestContextHolder来获取当前请求的上下文
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String businessId = request.getParameter("businessId");

        // 获取请求参数
        Map<String, Object> paramMap = new HashMap<>();
        //请求参数列表。
        Object[] args = joinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //获取方法请求参数的名称列表。
        String[] parameterNames = methodSignature.getParameterNames();
        for (int i = 0; i < parameterNames.length; i++) {
            if (!(args[i] instanceof HttpServletRequest) && !(args[i] instanceof HttpServletResponse)) {
                paramMap.put(parameterNames[i], args[i]);
            }
        }

        //布式锁和释放锁。
        Method method = methodSignature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
        String lockKey = distributedLock.key()+businessId;
        long lockTime = distributedLock.lockTime();

        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean lockAcquired = lock.tryLock(5,lockTime, TimeUnit.SECONDS);
            if (lockAcquired) {
                return joinPoint.proceed();
            } else {
                throw new RuntimeException("Failed to acquire lock: " + lockKey);
            }
        } finally {
            lock.unlock();
        }
    }
}
