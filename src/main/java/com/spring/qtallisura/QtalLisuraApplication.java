package com.spring.qtallisura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class QtalLisuraApplication {

    public static void main(String[] args) {
        SpringApplication.run(QtalLisuraApplication.class, args);
    }

}
