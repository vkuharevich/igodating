package com.igodating.chat.api.request;

import com.igodating.commons.security.SecurityRequestModifier;
import com.igodating.commons.security.SecurityRequestModify;
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
}
