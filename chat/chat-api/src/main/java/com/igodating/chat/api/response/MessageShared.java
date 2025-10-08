package com.igodating.chat.api.response;

import com.igodating.commons.model.FileAttachment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MessageShared {

    private long id;
    private ChatMemberShared creator;
    private String messageText;
    private String guid;
    private List<FileAttachment> attachments;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
