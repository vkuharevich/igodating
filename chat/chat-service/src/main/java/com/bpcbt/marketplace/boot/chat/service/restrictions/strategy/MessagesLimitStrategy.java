package com.bpcbt.marketplace.boot.chat.service.restrictions.strategy;

import com.bpcbt.marketplace.chat.api.request.CreateMessageRequest;
import com.bpcbt.marketplace.chat.db.api.ChatRepository;
import com.bpcbt.marketplace.chat.db.api.ChatRestrictionDto;
import lombok.AllArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;

@AllArgsConstructor
public class MessagesLimitStrategy implements RestrictionStrategy {

    private final ChatRepository chatRepository;
    private final MessageSourceAccessor messageSourceAccessor;

    @Override
    public RestrictionCheckResponse check(ChatRestrictionDto restriction, long chatId, CreateMessageRequest request) {
        int countMessageByDuration = this.chatRepository.getCountMessageByDuration(restriction.getDuration(), chatId, request.getProfileReferenceId());
        boolean passed = countMessageByDuration < restriction.getLimit();
        RestrictionCheckResponse.RestrictionCheckResponseBuilder responseBuilder = RestrictionCheckResponse.builder()
                .passed(passed);
        if (!passed)
            responseBuilder.message(messageSourceAccessor.getMessage("chat.restriction.messages-limit",
                    new Object[]{restriction.getLimit(), restriction.getDuration().toMinutes()}));
        return responseBuilder.build();
    }
}
