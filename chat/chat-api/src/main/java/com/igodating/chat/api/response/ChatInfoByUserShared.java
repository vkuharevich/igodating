package com.igodating.chat.api.response;

import com.igodating.chat.api.ChatType;
import com.igodating.commons.model.image.SimpleImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ChatInfoByUserShared {

    private int unreadMessages;
    private String chatTitle;
    private ChatType chatType;
    private MessageShared lastMessage;
    private SimpleImage avatar;
    private List<ChatMemberShared> chatMembers;
}
