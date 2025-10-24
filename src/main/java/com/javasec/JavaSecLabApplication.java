package com.javasec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Java安全漏洞靶场主应用
 * 
 * @author JavaSec Lab
 */
@SpringBootApplication
public class JavaSecLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaSecLabApplication.class, args);
        System.out.println("===========================================");
        System.out.println("Java Security Lab 启动成功!");
        System.out.println("访问地址: http://localhost:8080");
        System.out.println("===========================================");
    }
}