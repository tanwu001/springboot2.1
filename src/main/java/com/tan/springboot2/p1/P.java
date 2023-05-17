package com.tan.springboot2.p1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//(160条消息) 如果一个外卖配送单子要发布，现在有200个骑手都想要接这一单，如何保证只有一个骑手接到单子？_powerTan01的博客-CSDN博客
//https://blog.csdn.net/tanwu1/article/details/130721622

@RestController
@RequestMapping("/p1")
public class P {

    @Autowired
    OrderService orderService;

    @GetMapping("/m/{orderId}")
    public boolean m(@PathVariable Integer orderId) {
        return orderService.takeOrder(orderId);
    }

}
