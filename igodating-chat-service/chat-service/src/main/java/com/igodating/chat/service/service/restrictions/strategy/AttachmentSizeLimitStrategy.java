package com.igodating.chat.service.service.restrictions.strategy;

import com.igodating.chat.api.request.CreateMessageRequest;
import com.igodating.chat.db.api.ChatRestrictionDto;
import lombok.AllArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.unit.DataSize;

@AllArgsConstructor
public class AttachmentSizeLimitStrategy implements RestrictionStrategy {

    private final MessageSourceAccessor messageSourceAccessor;

    @Override
    public RestrictionCheckResponse check(ChatRestrictionDto restriction, long chatId, CreateMessageRequest request) {
        boolean passed = request.getAttachments().stream()
                .map(x -> DataSize.ofBytes(x.getMetadata().getFileSize()).toMegabytes())
                .max(Long::compareTo)
                .orElse(0L) <= restriction.getLimit();
        RestrictionCheckResponse.RestrictionCheckResponseBuilder responseBuilder = RestrictionCheckResponse.builder()
                .passed(passed);
        if (!passed) responseBuilder.message(messageSourceAccessor.getMessage("chat.restriction.attachments-size-limit",
                new Object[]{restriction.getLimit()}));
        return responseBuilder.build();
    }
}
