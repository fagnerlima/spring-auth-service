package br.pro.fagnerlima.spring.auth.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpringAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAuthServiceApplication.class, args);
    }

}
