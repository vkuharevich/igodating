package com.igodating.chat.db.api.dto;

import com.igodating.chat.api.ChatType;
import com.igodating.commons.model.image.SimpleImage;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ChatInfoByUserDto {

    private long id;
    private int unreadMessages;
    private String chatTitle;
    private ChatType chatType;
    private MessageDto lastMessage;
    private SimpleImage avatar;
    private List<ChatMemberDto> chatMembers;
}
