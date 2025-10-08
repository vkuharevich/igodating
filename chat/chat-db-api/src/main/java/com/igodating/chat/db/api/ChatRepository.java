package com.igodating.chat.db.api;

import com.igodating.chat.api.ChatType;
import com.igodating.chat.api.request.ChatCreateRequest;
import com.igodating.chat.api.request.CreateMessageRequest;
import com.igodating.chat.api.request.GetChatRequest;
import com.igodating.chat.api.request.GetChatsByPageRequest;
import com.igodating.chat.api.request.GetMessagesByPageRequest;
import com.igodating.chat.db.api.dto.ChatDto;
import com.igodating.chat.db.api.dto.ChatInfoByUserDto;
import com.igodating.chat.db.api.dto.ChatMemberCreateDto;
import com.igodating.chat.db.api.dto.ChatPaginationDto;
import com.igodating.chat.db.api.dto.MessageDto;
import com.igodating.commons.global.ProfileType;
import com.igodating.commons.model.image.SimpleImage;
import com.igodating.commons.model.page.Page;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ChatRepository {
    Set<String> getAllowedFilesExtensions(FileOperation operationName);

    Long createChat(ChatCreateRequest request);

    boolean updateCreator(long chatId, long chatMemberId);

    ChatDto getById(long chatId, GetChatRequest request, long messageLimit);

    Page<ChatPaginationDto> getByPage(GetChatsByPageRequest request);

    MessageDto createMessage(long chatId, CreateMessageRequest request);

    Page<MessageDto> getMessagesByPage(GetMessagesByPageRequest request);

    Long addChatMember(long chatId, ChatMemberCreateDto dto);

    boolean addChatMembers(long chatId, Collection<ChatMemberCreateDto> createDtoList);

    Set<Long> getMembersByChatId(long chatId);

    boolean leaveChat(long chatId, long profileId);

    List<ChatInfoByUserDto> getChatSummaryByUser(Long profileId);

    Map<ChatType, Long> getAllUnreadMessageCountGroupedByChatType(Long profileId);

    boolean readMessages(long chatId, long profileId, Collection<Long> messageIds);

    Set<Pair<Long, ProfileType>> getMandatoryMembers(ChatType chatType);

    boolean uploadChatAvatar(long chatId, SimpleImage image);

    int getCountMessageByDuration(Duration duration, long chatId, long profileReferenceId);

    int getCountAttachmentsByDuration(Duration duration, long chatId, long profileReferenceId);

    Set<ChatRestrictionDto> getChatRestrictions();
}
