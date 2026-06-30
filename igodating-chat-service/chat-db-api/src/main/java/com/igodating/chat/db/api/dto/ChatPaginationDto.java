package com.igodating.chat.db.api.dto;

import com.igodating.chat.api.ChatType;
import com.igodating.commons.model.image.SimpleImage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ChatPaginationDto {

    private long id;
    private String title;
    private ChatType type;
    private SimpleImage avatar;
    private MessageDto lastMessage;
    private List<ChatMemberDto> chatMembers;
}
