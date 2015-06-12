package com.canoo.dolphin.todo.server;

import com.canoo.dolphin.server.spring.DolphinPlatformApplication;
import com.canoo.dolphin.server.spring.DolphinPlatformSpringBootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@DolphinPlatformApplication
@EnableJpaRepositories
public class ToDoServer {

    public static void main(String... args) {
        SpringApplication.run(ToDoServer.class, args);
    }

}
