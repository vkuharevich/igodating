package com.bpcbt.marketplace.chat.api.response;

import com.bpcbt.marketplace.chat.api.ChatType;
import com.bpcbt.marketplace.commons.model.image.SimpleImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatPaginationShared {

    private long id;
    private String title;
    private ChatType type;
    private SimpleImage avatar;
    private MessageShared lastMessage;
    private List<ChatMemberShared> chatMembers;
}
