package com.bpcbt.marketplace.chat.db.api;

public interface RestrictionStrategy {

    boolean check(ChatRestrictionDto restriction);
}
