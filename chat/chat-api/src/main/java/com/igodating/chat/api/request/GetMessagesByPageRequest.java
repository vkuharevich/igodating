package com.igodating.chat.api.request;

import com.igodating.boot.user.api.global.security.jwt.JwtSecurityUser;
import com.igodating.boot.user.api.request.SecurityRequestModifier;
import com.igodating.boot.user.api.validation.SecurityRequestModify;
import com.igodating.commons.common.request.PaginationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SecurityRequestModify
@Builder
public class GetMessagesByPageRequest implements SecurityRequestModifier {

    @NotNull
    @Valid
    private PaginationRequest paginationRequest;
    @Min(1)
    private long chatId;
    private Long userId;

    @Override
    public void modifyRequest(JwtSecurityUser principal) {
        userId = principal.getCurrentProfileReferenceId();
    }
}
