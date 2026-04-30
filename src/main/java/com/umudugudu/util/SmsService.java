package com.umudugudu.util;

import org.springframework.stereotype.Service;

@Service
public class SmsService {

    public void sendSms(String phone,String msg){
        System.out.println("SMS sent to "+phone+" : "+msg);
    }
}
