package com.igodating.chat.service;

import com.igodating.commons.dto.ServiceEnum;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.expression.spel.SpelCompilerMode;

@SpringBootApplication
//@EnableConfigurationProperties({ApplicationClientsProperties.class, JwtBackendProperties.class})
//@EnableDiscoveryClient
public class ChatServiceMain {

    static {
        System.setProperty("spring.expression.compiler.mode", SpelCompilerMode.MIXED.name());
        ServiceEnum.setCurrentService(ServiceEnum.CHAT);
    }

    public static void main(String[] args) {
        SpringApplication.run(ChatServiceMain.class, args);
    }
}
