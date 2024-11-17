package com.igodating.questionary.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

public class CurrentUserInfo {

    public static String getUserId() {
        return ((DefaultOidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserInfo().getClaim("sub");
    }
}
