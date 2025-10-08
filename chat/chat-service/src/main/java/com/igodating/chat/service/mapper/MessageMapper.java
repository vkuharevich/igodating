package com.igodating.chat.service.mapper;

import com.igodating.chat.api.response.ChatInfoByUserShared;
import com.igodating.chat.api.response.ChatPaginationShared;
import com.igodating.chat.api.response.ChatShared;
import com.igodating.chat.api.response.MessageShared;
import com.igodating.chat.db.api.dto.ChatDto;
import com.igodating.chat.db.api.dto.ChatInfoByUserDto;
import com.igodating.chat.db.api.dto.ChatPaginationDto;
import com.igodating.chat.db.api.dto.MessageDto;
import org.mapstruct.Mapper;

@Mapper
public interface MessageMapper {

    MessageShared mapMessageToShared(MessageDto message);

    ChatShared mapChatToShared(ChatDto chat);

    ChatPaginationShared mapChatToPaginationShared(ChatPaginationDto chat);

    ChatInfoByUserShared mapToMetaDataShared(ChatInfoByUserDto dto);
}
