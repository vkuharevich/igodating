package com.igodating.commons.security;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties(prefix = "jwt.keys")
@Validated
public class JwtBackendProperties {
    @NotEmpty
    private String back2BackKey;
    @NotEmpty
    private String clientPublicKey;
}
