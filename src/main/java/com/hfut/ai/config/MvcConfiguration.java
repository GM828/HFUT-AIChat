package com.hfut.ai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置
 * author: GM
 */
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") //  允许所有路径
                .allowedOrigins("*") // 允许所有域
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD") // 允许所有请求方法
                .allowedHeaders("*") //  允许所有请求头
                .exposedHeaders("*"); //  允许所有响应头
    }
}
