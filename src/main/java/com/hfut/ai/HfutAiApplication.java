package com.hfut.ai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.hfut.ai.mapper")
@SpringBootApplication()
public class HfutAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HfutAiApplication.class, args);
    }

}
