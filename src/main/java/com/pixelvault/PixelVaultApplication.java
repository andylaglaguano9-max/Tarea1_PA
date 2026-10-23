package com.pixelvault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class PixelVaultApplication {

    public static void main(String[] args) {
        SpringApplication.run(PixelVaultApplication.class, args);
    }
}
