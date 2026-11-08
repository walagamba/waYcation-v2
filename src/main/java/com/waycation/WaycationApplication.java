package com.waycation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class WaycationApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaycationApplication.class, args);
    }
}
