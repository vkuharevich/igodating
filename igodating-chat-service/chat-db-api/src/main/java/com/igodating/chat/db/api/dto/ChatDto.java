package com.igodating.chat.db.api.dto;

import com.igodating.chat.api.ChatType;
import com.igodating.commons.model.image.SimpleImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ChatDto {

    private long id;
    private String title;
    private ChatType type;
    private SimpleImage avatar;
    private List<MessageDto> messages;
    private List<MessageDto> unreadMessages;
}
