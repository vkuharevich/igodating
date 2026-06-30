package com.igodating.chat.api.request;

import com.igodating.commons.security.SecurityRequestModifier;
import com.igodating.commons.security.SecurityRequestModify;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@SecurityRequestModify
@Builder
public class GetChatRequest implements SecurityRequestModifier {

    private Long profileId;
}
