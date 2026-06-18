package com.igodating.user.connector;

import com.fasterxml.jackson.core.type.TypeReference;
import com.igodating.commons.connector.BaseConnector;
import com.igodating.commons.connector.RequestWrapper;
import com.igodating.commons.dto.ResponseWrapper;
import com.igodating.user.dto.UserDto;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

public class UserServiceConnector extends BaseConnector {

    public UserServiceConnector(WebClient webClient, JsonMapper mapper) {
        super(webClient, "http://user-service:9002", mapper);
    }

    public Mono<ResponseWrapper<UserDto>> getUser(Long userId) {
        return this.getAction("/api/users/{id}", new TypeReference<>() {
        }, RequestWrapper.empty(), userId);
    }
}
