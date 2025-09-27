package com.bpcbt.marketplace.boot.chat.service.restrictions.strategy;

import com.bpcbt.marketplace.chat.api.request.CreateMessageRequest;
import com.bpcbt.marketplace.chat.db.api.ChatRestrictionDto;
import com.bpcbt.marketplace.commons.model.FileAttachment;
import org.springframework.util.unit.DataSize;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LimitAttachmentSizeStrategy implements RestrictionStrategy {

    private static final String RESTRICTION_NOT_PASSED = "Attachment size is too big. Size limit %s mb";

    @Override
    public RestrictionCheckResponse check(ChatRestrictionDto restriction, long chatId, CreateMessageRequest request) {
        final List<FileAttachment> attachments = Optional.ofNullable(request.getAttachments()).orElse(Collections.emptyList());
        final boolean passed = attachments.stream()
                .map(x -> DataSize.ofBytes(x.getMetadata().getFileSize()).toMegabytes())
                .max(Long::compareTo)
                .orElse(0L) < restriction.getLimit();
        final RestrictionCheckResponse.RestrictionCheckResponseBuilder responseBuilder = RestrictionCheckResponse.builder()
                .passed(passed);
        if (!passed) responseBuilder.message(String.format(RESTRICTION_NOT_PASSED, restriction.getLimit()));
        return responseBuilder.build();
    }
}
