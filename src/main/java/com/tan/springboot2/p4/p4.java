package com.tan.springboot2.p4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/p4")
public class p4 {

    @Autowired
    SmsVerificationCodeService smsVerificationCodeService;

    @GetMapping("/send")
    public boolean send( String mobile) {
        return smsVerificationCodeService.sendVerificationCode(mobile);
    }

    @GetMapping("/verify")
    public boolean verify(String mobile, String verificationCode) {
        return smsVerificationCodeService.verifyVerificationCode(mobile, verificationCode);
    }

}
