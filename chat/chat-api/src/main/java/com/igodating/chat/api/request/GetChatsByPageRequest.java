package com.igodating.chat.api.request;

import com.igodating.chat.api.ChatType;
import com.igodating.commons.security.SecurityRequestModifier;
import com.igodating.commons.security.SecurityRequestModify;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SecurityRequestModify
public class GetChatsByPageRequest implements SecurityRequestModifier {

    @NotNull
    @Valid
    private PaginationRequest paginationRequest;
    private ChatType type;
    private Long profileId;
    private ProfileType profileType;
}
