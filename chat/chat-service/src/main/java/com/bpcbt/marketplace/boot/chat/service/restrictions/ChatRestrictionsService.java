package com.bpcbt.marketplace.boot.chat.service.restrictions;

import com.bpcbt.marketplace.boot.chat.exception.CheckChatRestrictionException;
import com.bpcbt.marketplace.boot.chat.service.restrictions.strategy.*;
import com.bpcbt.marketplace.chat.api.request.CreateMessageRequest;
import com.bpcbt.marketplace.chat.db.api.ChatRepository;
import com.bpcbt.marketplace.chat.db.api.ChatRestrictionDto;
import com.bpcbt.marketplace.chat.db.api.ChatRestrictionType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatRestrictionsService {

    private final ChatRepository chatRepository;
    private final MessageSourceAccessor messageSourceAccessor;
    private final Map<ChatRestrictionType, RestrictionStrategy> type2RestrictionStrategy;
    private final Set<ChatRestrictionDto> restrictions;

    @PostConstruct
    public void init() {
        for (ChatRestrictionType type : ChatRestrictionType.values()) {
            RestrictionStrategy strategy = switch (type) {
                case LIMIT_CHAT_MESSAGES -> new MessagesLimitStrategy(chatRepository, messageSourceAccessor);
                case LIMIT_CHAT_ATTACHMENTS -> new AttachmentsLimitStrategy(chatRepository, messageSourceAccessor);
                case LIMIT_SIZE_MESSAGE_ATTACHMENTS -> new AttachmentSizeLimitStrategy(messageSourceAccessor);
            };
            type2RestrictionStrategy.put(type, strategy);
        }
        restrictions.addAll(chatRepository.getChatRestrictions());
    }


    public CheckResponse check(long chatId, CreateMessageRequest request) {
        final List<String> notPassedRestrictionsMessages = restrictions.stream()
                .map(restriction -> Optional.ofNullable(type2RestrictionStrategy.get(restriction.getType()))
                        .orElseThrow(() -> new CheckChatRestrictionException("Check logic not implemented for type : " + restriction.getType().name()))
                        .check(restriction, chatId, request))
                .filter(x -> !x.isPassed())
                .map(RestrictionCheckResponse::getMessage)
                .toList();
        return notPassedRestrictionsMessages.isEmpty() ? CheckResponse.success() : new CheckResponse(notPassedRestrictionsMessages);
    }
}
