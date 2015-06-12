package com.canoo.dolphin.demo.spring;

import com.canoo.dolphin.server.spring.DolphinPlatformApplication;
import com.canoo.dolphin.server.spring.DolphinPlatformSpringBootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@DolphinPlatformApplication
public class Application {

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

}
