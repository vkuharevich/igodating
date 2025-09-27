package com.bpcbt.marketplace.boot.chat.service.restrictions.strategy;

import lombok.Getter;

import java.util.List;

@Getter
public class CheckResponse {
    private final List<String> messages;
    private final boolean allCheckPassed;

    public CheckResponse(List<String> messages) {
        this.messages = messages;
        this.allCheckPassed = false;
    }

    private CheckResponse(boolean allCheckPassed) {
        this.allCheckPassed = allCheckPassed;
        this.messages = null;
    }

    public static CheckResponse success() {
        return new CheckResponse(true);
    }
}
