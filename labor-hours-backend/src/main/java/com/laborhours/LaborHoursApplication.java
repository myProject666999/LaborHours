package com.laborhours;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.laborhours.mapper")
public class LaborHoursApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaborHoursApplication.class, args);
    }
}
