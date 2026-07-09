package com.igodating.dto.questionary.create;

import com.igodating.commons.security.SecurityRequestModifier;
import com.igodating.commons.security.SecurityRequestModify;
import com.igodating.commons.security.models.JwtUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SecurityRequestModify
public class QuestionaryCreateDto implements SecurityRequestModifier {

    private String name;

    private Long questionaryTypeId;

    private Long userId;

    @Override
    public void modifyRequest(JwtUser principal) {
        this.userId = principal.getJwtUser().getId();
    }
}
