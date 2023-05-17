package com.tan.springboot2.p1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/c1")
public class C {

    @Autowired
    OrderService orderService;

    @GetMapping("/t1/{id}")
    public boolean t1(@PathVariable Integer id) {
        return orderService.takeOrder(id);
    }

}
