package com.igodating.chat.api.response;

import lombok.Getter;

import java.util.List;

@Getter
public class CheckRestrictionsResponse {
    private final List<String> messages;
    private final boolean allCheckPassed;

    public CheckRestrictionsResponse(List<String> messages) {
        this.messages = messages;
        this.allCheckPassed = false;
    }

    private CheckRestrictionsResponse(boolean allCheckPassed) {
        this.allCheckPassed = allCheckPassed;
        this.messages = null;
    }

    public static CheckRestrictionsResponse success() {
        return new CheckRestrictionsResponse(true);
    }
}
