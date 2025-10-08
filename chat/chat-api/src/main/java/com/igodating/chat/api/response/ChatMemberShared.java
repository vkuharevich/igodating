package com.igodating.chat.api.response;

import com.igodating.commons.global.ProfileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ChatMemberShared {

    private long id;
    private long profileId;
    private String fullName;
    private ProfileType profileType;
    private boolean kicked;
    private long chatId;
}
