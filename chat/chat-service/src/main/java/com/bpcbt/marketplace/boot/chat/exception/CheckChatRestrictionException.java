package com.bpcbt.marketplace.boot.chat.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class CheckChatRestrictionException extends RuntimeException {

    private final List<String> restrictionNotPassedMessages;

    public CheckChatRestrictionException(String message) {
        super(message);
        restrictionNotPassedMessages = null;
    }

    public CheckChatRestrictionException(String message, List<String> restrictionNotPassedMessages) {
        super(message);
        this.restrictionNotPassedMessages = restrictionNotPassedMessages;
    }
}
