package com.igodating.chat.service.config;

import com.igodating.boot.commons.config.ExceptionDetailsConfiguration;
import com.igodating.boot.commons.properties.JwtBackendProperties;
import com.igodating.boot.events_log.aspect.RestTemplateAuditHeadersProxyInterceptor;
import com.igodating.boot.media.api.connector.MediaServiceConnector;
import com.igodating.boot.user.api.auth.BackendHeaderThreadLocalServletExchangeFilterFunction;
import com.igodating.boot.user.api.auth.RestTemplateBackendHeaderUserAuthorizedModifierInterceptor;
import com.igodating.boot.user.api.connector.UserServiceConnector;
import com.igodating.boot.user.api.global.security.JwtConstants;
import com.igodating.commons.model.lang.Lang;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.DeferringLoadBalancerExchangeFilterFunction;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.util.unit.DataSize;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Configuration
@EnableScheduling
@EnableMethodSecurity
@Import({ExceptionDetailsConfiguration.class})
public class AppConfig {

    @Bean
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate(RestTemplateBuilder builder,
                                                 @Autowired(required = false) RestTemplateAuditHeadersProxyInterceptor restTemplateAuditHeadersProxyInterceptor,
                                                 @Value("${spring.application.name}") String appName,
                                                 ObjectMapper objectMapper,
                                                 JwtBackendProperties jwtBackendProperties) {

        List<ClientHttpRequestInterceptor> clientHttpRequestInterceptors = new ArrayList<>();
        clientHttpRequestInterceptors.add(new RestTemplateBackendHeaderUserAuthorizedModifierInterceptor("Bearer " + Jwts.builder()
                .issuer(appName)
                .claim(JwtConstants.AUTHORITIES, JwtConstants.BACKEND_AUTHORITY)
                .signWith(Keys.hmacShaKeyFor(jwtBackendProperties.getBack2BackKey().getBytes()))
                .compact(),
                objectMapper,
                appName, Keys.hmacShaKeyFor(jwtBackendProperties.getBack2BackKey().getBytes())));
        if (restTemplateAuditHeadersProxyInterceptor != null) {
            clientHttpRequestInterceptors.add(restTemplateAuditHeadersProxyInterceptor);
        }

        return builder.additionalInterceptors(clientHttpRequestInterceptors)
                .build();
    }

    @Bean
    public BackendHeaderThreadLocalServletExchangeFilterFunction backendHeaderFilterFunction(ObjectMapper objectMapper,
                                                                                             @Value("${spring.application.name}") String appName,
                                                                                             JwtBackendProperties jwtBackendProperties) {
        return new BackendHeaderThreadLocalServletExchangeFilterFunction(objectMapper, appName, Keys.hmacShaKeyFor(jwtBackendProperties.getBack2BackKey().getBytes()));
    }

    @Bean
    public WebClient loadBalancedWebClient(DeferringLoadBalancerExchangeFilterFunction<?> function,
                                           BackendHeaderThreadLocalServletExchangeFilterFunction filterFunction,
                                           @Value("${spring.application.name}") String appName, WebClient.Builder builder,
                                           JwtBackendProperties jwtBackendProperties) {
        return builder
                .filter(function)
                .filter(filterFunction)
                .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().maxInMemorySize(Math.toIntExact(DataSize.ofMegabytes(2).toBytes())))
                .defaultHeader(JwtConstants.HEADER_BACKEND_AUTHORIZATION, "Bearer " + Jwts.builder()
                        .issuer(appName)
                        .claim(JwtConstants.AUTHORITIES, JwtConstants.BACKEND_AUTHORITY)
                        .signWith(Keys.hmacShaKeyFor(jwtBackendProperties.getBack2BackKey().getBytes()))
                        .compact())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:i18n/messages");
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return messageSource;
    }

    @Bean
    public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
        return new MessageSourceAccessor(messageSource, Locale.forLanguageTag(Lang.DEFAULT_LANG.getLabel()));
    }

    @Bean
    public MediaServiceConnector contentServiceConnector(RestTemplate loadBalancedRestTemplate, ObjectMapper mapper) {
        return new MediaServiceConnector(loadBalancedRestTemplate);
    }

    @Bean
    public UserServiceConnector userServiceConnector(RestTemplate loadBalancedRestTemplate, ObjectMapper mapper) {
        return new UserServiceConnector(loadBalancedRestTemplate);
    }
}
