package com.ssafy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.ssafy.**.mapper")
//@ComponentScan(basePackages = {
//        "com.ssafy.local",
//        "com.ssafy.user",
//        "com.ssafy.restaurant",
//        "com.ssafy.search",
//        "com.ssafy.follow",
//        "com.ssafy.**"
//})
public class HoSuApplication {

    public static void main(String[] args) {
        SpringApplication.run(HoSuApplication.class, args);

    }
}