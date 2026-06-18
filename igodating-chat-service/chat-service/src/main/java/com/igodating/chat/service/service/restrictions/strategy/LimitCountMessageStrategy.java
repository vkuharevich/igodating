package com.igodating.chat.service.service.restrictions.strategy;

import com.igodating.chat.api.request.CreateMessageRequest;
import com.igodating.chat.db.api.ChatRepository;
import com.igodating.chat.db.api.ChatRestrictionDto;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LimitCountMessageStrategy implements RestrictionStrategy {

    private final ChatRepository chatRepository;
    private static final String RESTRICTION_NOT_PASSED = "Too many message for period time. Limit %s message in %s minutes";

    @Override
    public RestrictionCheckResponse check(ChatRestrictionDto restriction, long chatId, CreateMessageRequest request) {
        final boolean passed = this.chatRepository.getCountMessageByDuration(restriction.getDuration(), chatId, request.getProfileReferenceId()) < restriction.getLimit();
        final RestrictionCheckResponse.RestrictionCheckResponseBuilder responseBuilder = RestrictionCheckResponse.builder()
                .passed(passed);
        if (!passed)
            responseBuilder.message(String.format(RESTRICTION_NOT_PASSED, restriction.getLimit(), restriction.getDuration().toMinutes()));
        return responseBuilder.build();
    }
}
