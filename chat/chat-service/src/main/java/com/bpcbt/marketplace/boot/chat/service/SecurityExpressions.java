package com.bpcbt.marketplace.boot.chat.service;

import com.bpcbt.marketplace.boot.user.api.global.security.jwt.JwtSecurityUser;
import com.bpcbt.marketplace.boot.user.api.global.security.jwt.JwtUserWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityExpressions {

    private final ChatCache chatCache;

    public boolean checkUserHasAccessToChat(long chatId, Object principal) {
        if (principal instanceof JwtUserWrapper principalWrapper) {
            final JwtSecurityUser jwtUser = principalWrapper.getJwtUser();
            if (jwtUser.isOperator()) return true;
            return chatCache.getMembersByChat(chatId).contains(jwtUser.getCurrentProfileReferenceId());
        }
        return true;
    }

    public boolean checkUserHasAccessToChats(List<Long> chatIds, Object principal) {
        if (principal instanceof JwtUserWrapper principalWrapper) {
            final JwtSecurityUser jwtUser = principalWrapper.getJwtUser();
            if (jwtUser.isOperator()) return true;
            return chatCache.getMembersByChats(chatIds).contains(jwtUser.getCurrentProfileReferenceId());
        }
        return true;
    }
}
