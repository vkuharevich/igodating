package com.igodating.questionary.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableFeignClients(basePackages = { "com.igodating.questionary.feign" })
@EnableWebMvc
public class ApplicationConfig {


}
