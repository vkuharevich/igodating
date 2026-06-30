package com.igodating.chat.api.request;

import com.igodating.boot.user.api.global.security.jwt.JwtSecurityUser;
import com.igodating.boot.user.api.request.SecurityRequestModifier;
import com.igodating.boot.user.api.validation.SecurityRequestModify;
import jakarta.validation.constraints.Min;
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
public class GetChatSummaryRequest implements SecurityRequestModifier {

    @Min(1)
    private Long profileId;

    @Override
    public void modifyRequest(JwtSecurityUser principal) {
        this.profileId = principal.getCurrentProfileReferenceId();
    }
}
