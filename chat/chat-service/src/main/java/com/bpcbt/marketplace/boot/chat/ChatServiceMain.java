package com.bpcbt.marketplace.boot.chat;

import com.bpcbt.marketplace.boot.commons.properties.ApplicationClientsProperties;
import com.bpcbt.marketplace.boot.commons.properties.JwtBackendProperties;
import com.bpcbt.marketplace.commons.model.ServiceEnum;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.expression.spel.SpelCompilerMode;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationClientsProperties.class, JwtBackendProperties.class})
@EnableDiscoveryClient
public class ChatServiceMain {

    static {
        System.setProperty("spring.expression.compiler.mode", SpelCompilerMode.MIXED.name());
        ServiceEnum.setCurrentService(ServiceEnum.CHAT);
    }

    public static void main(String[] args) {
        SpringApplication.run(ChatServiceMain.class, args);
    }
}
