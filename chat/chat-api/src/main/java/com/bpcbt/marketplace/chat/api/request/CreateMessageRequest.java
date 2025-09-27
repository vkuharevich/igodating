package com.bpcbt.marketplace.chat.api.request;

import com.bpcbt.marketplace.boot.user.api.global.security.jwt.JwtSecurityUser;
import com.bpcbt.marketplace.boot.user.api.request.SecurityRequestModifier;
import com.bpcbt.marketplace.boot.user.api.validation.SecurityRequestModify;
import com.bpcbt.marketplace.commons.model.FileAttachment;
import com.bpcbt.marketplace.commons.validation.NotAllNullFields;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NotAllNullFields(fields = {"attachments", "messageText"})
@SecurityRequestModify
public class CreateMessageRequest implements SecurityRequestModifier {

    private String guid;
    @Valid
    private List<@NotNull FileAttachment> attachments;
    private String messageText;
    private Long profileReferenceId;

    @Override
    public void modifyRequest(JwtSecurityUser principal) {
        profileReferenceId = principal.getCurrentProfileReferenceId();
    }
}
