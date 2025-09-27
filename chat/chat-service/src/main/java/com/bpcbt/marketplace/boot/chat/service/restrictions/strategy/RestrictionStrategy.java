package com.bpcbt.marketplace.boot.chat.service.restrictions.strategy;

import com.bpcbt.marketplace.chat.api.request.CreateMessageRequest;
import com.bpcbt.marketplace.chat.db.api.ChatRestrictionDto;

public interface RestrictionStrategy {

    RestrictionCheckResponse check(ChatRestrictionDto restriction, long chatId, CreateMessageRequest request);
}
