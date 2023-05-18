package com.tan.springboot2.p5;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/p5")
public class P5 {
    @PostMapping("/m")
    public Student m(@RequestBody Student stu) {
        return stu;
    }
}
