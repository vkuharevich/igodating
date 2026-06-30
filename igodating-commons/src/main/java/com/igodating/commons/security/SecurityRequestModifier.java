package com.igodating.commons.security;

import com.igodating.commons.security.models.JwtUser;

public interface SecurityRequestModifier {

    void modifyRequest(JwtUser principal);
}
