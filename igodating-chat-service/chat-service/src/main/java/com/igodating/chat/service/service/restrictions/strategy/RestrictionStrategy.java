package com.igodating.chat.service.service.restrictions.strategy;

import com.igodating.chat.api.request.CreateMessageRequest;
import com.igodating.chat.db.api.ChatRestrictionDto;

public interface RestrictionStrategy {

    RestrictionCheckResponse check(ChatRestrictionDto restriction, long chatId, CreateMessageRequest request);
}
