package com.igodating.chat.api.request;

import com.igodating.chat.api.ChatType;
import com.igodating.commons.security.models.JwtUser;
import com.igodating.commons.security.SecurityRequestModifier;
import com.igodating.commons.security.SecurityRequestModify;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@SecurityRequestModify
@Builder
public class ChatCreateRequest implements SecurityRequestModifier {

    @NotNull
    private String title;
    @NotNull
    private ChatType type;
    private Long entityId;
    private Long creatorId;
    @NotEmpty
    private Set<Long> opponentIdSet;

    @Override
    public void modifyRequest(JwtUser principal) {
        creatorId = principal.getId();
    }
}
