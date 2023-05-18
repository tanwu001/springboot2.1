package com.tan.springboot2.p3;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/p3")
public class P3 {

        @DistributedLock(key = "myLockKey:",lockTime = 30)
        @GetMapping("/myEndpoint")
        public String myEndpoint(@RequestParam String businessId, @RequestParam String name, HttpServletRequest request, HttpServletResponse response) throws InterruptedException {
            // 在这里实现需要加锁的业务逻辑
            Thread.sleep(28*1000L);
            return "Locked method executed";
        }
}
