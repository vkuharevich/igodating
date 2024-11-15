package com.igodating.questionary.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = { "com.igodating.questionary.feign" })
public class ApplicationConfig {


}
