package com.bpcbt.marketplace.boot.chat.service.restrictions.strategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class RestrictionCheckResponse {

    private boolean passed;
    private String message;
}
