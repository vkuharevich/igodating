package com.igodating.chat.db.api.dto;

import com.igodating.commons.global.ProfileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class ChatMemberCreateDto {

    private long profileId;
    private ProfileType profileType;
    private String userName;
}
