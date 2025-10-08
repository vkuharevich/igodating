package com.igodating.chat.service;

import com.igodating.chat.service.service.ChatService;
import com.igodating.chat.service.service.restrictions.ChatRestrictionsService;
import com.igodating.boot.commons.config.ExceptionDetailsConfiguration;
import com.igodating.boot.user.api.connector.UserServiceAsyncConnector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-swagger-generate-test.properties")
@ActiveProfiles("test")
public class SwaggerGenerateApiTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private BuildProperties buildProperties;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    public void generateJson() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs").accept(MediaType.APPLICATION_JSON))
                .andDo((result) -> {
                    Path pathToFile = Paths.get("docs/swagger/" + buildProperties.getArtifact() + "-swagger.json");
                    Files.createDirectories(pathToFile.getParent());
                    result.getResponse().setCharacterEncoding("UTF-8");
                    final String contentAsString = result.getResponse().getContentAsString();
                    try (PrintWriter out = new PrintWriter(pathToFile.toFile())) {
                        out.println(contentAsString);
                    }
                });
    }

    @SpringBootApplication(scanBasePackages = "com.igodating.boot.chat.controller", exclude = {
            DataSourceAutoConfiguration.class,
            UserDetailsServiceAutoConfiguration.class,
            SecurityAutoConfiguration.class,
            ManagementWebSecurityAutoConfiguration.class})
    @Import({ExceptionDetailsConfiguration.class})
    public static class TestConfig {
        @MockitoBean
        ChatService chatService;
        @MockitoBean
        ChatRestrictionsService chatRestrictionsService;
        @MockitoBean
        UserServiceAsyncConnector userServiceAsyncConnector;
    }
}