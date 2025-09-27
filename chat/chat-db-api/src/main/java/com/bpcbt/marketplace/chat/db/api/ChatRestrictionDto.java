package com.bpcbt.marketplace.chat.db.api;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class ChatRestrictionDto {

    private long id;
    private ChatRestrictionType type;
    private Duration duration;
    private boolean active;
    private int limit;
}
