package com.igodating.commons.security;

public interface JwtUserWrapper {

    JwtUser getJwtUser();

    boolean isBackendWrapper();
}
