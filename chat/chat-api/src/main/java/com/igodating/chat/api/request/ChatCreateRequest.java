package com.igodating.chat.api.request;

import com.igodating.chat.api.ChatType;
import com.igodating.commons.security.JwtUser;
import com.igodating.commons.security.SecurityRequestModifier;
import com.igodating.commons.security.SecurityRequestModify;
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
    private Long creatorProfileId;
    private Set<Opponent> opponents;

    @Override
    public void modifyRequest(JwtUser principal) {
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Opponent {
        private long profileId;
        private boolean legal;
    }
}
