package com.igodating.chat.db.api;

public interface RestrictionStrategy {

    boolean check(ChatRestrictionDto restriction);
}
