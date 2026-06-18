package com.igodating.chat.service.service.restrictions.strategy;

import com.igodating.chat.api.request.CreateMessageRequest;
import com.igodating.chat.db.api.ChatRepository;
import com.igodating.chat.db.api.ChatRestrictionDto;
import lombok.AllArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;

@AllArgsConstructor
public class AttachmentsLimitStrategy implements RestrictionStrategy {

    private final ChatRepository chatRepository;
    private final MessageSourceAccessor messageSourceAccessor;

    @Override
    public RestrictionCheckResponse check(ChatRestrictionDto restriction, long chatId, CreateMessageRequest request) {
        int countAttachmentsByDuration = this.chatRepository.getCountAttachmentsByDuration(restriction.getDuration(), chatId, request.getProfileReferenceId());
        boolean passed = countAttachmentsByDuration < restriction.getLimit();
        RestrictionCheckResponse.RestrictionCheckResponseBuilder responseBuilder = RestrictionCheckResponse.builder()
                .passed(passed);
        if (!passed) responseBuilder.message(messageSourceAccessor.getMessage("chat.restriction.attachments-limit",
                new Object[]{restriction.getLimit(), restriction.getDuration().toMinutes()}));
        return responseBuilder.build();
    }
}
