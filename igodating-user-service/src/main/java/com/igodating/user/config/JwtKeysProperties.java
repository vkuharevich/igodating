package com.igodating.user.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
@Accessors(chain = true)
@Validated
public class JwtKeysProperties {

    @Valid
    private Keys keys = new Keys();
    @DurationUnit(ChronoUnit.MINUTES)
    @NotNull
    private Duration accessLifetime;
    @DurationUnit(ChronoUnit.MINUTES)
    @NotNull
    private Duration refreshLifetime;

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Keys {
        @NotEmpty
        private String privateKey;
        @NotEmpty
        private String publicKey;
        private String basicKey;
    }
}
