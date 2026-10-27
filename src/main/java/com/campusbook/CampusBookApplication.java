package com.campusbook;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园二手书交易管理系统 启动类
 */
@SpringBootApplication
@MapperScan("com.campusbook.repository")
public class CampusBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusBookApplication.class, args);
    }
}
