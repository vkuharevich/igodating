package com.igodating.chat.service.service.restrictions.strategy;

import com.igodating.chat.api.request.CreateMessageRequest;
import com.igodating.chat.db.api.ChatRestrictionDto;

import java.util.List;
import java.util.Optional;

public class LimitCountMessageAttachmentsStrategy implements RestrictionStrategy {

    private static final String RESTRICTION_NOT_PASSED = "Too many attachments in one message. Count limit %s";

    @Override
    public RestrictionCheckResponse check(ChatRestrictionDto restriction, long chatId, CreateMessageRequest request) {
        final Integer attachmentsSize = Optional.ofNullable(request.getAttachments()).map(List::size).orElse(0);
        final boolean passed = attachmentsSize < restriction.getLimit();
        final RestrictionCheckResponse.RestrictionCheckResponseBuilder responseBuilder = RestrictionCheckResponse.builder()
                .passed(passed);
        if (!passed) responseBuilder.message(String.format(RESTRICTION_NOT_PASSED, restriction.getLimit()));
        return responseBuilder.build();
    }
}
