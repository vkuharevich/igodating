package com.igodating.commons.security;

public interface SecurityRequestModifier {

    void modifyRequest(JwtUser principal);
}
