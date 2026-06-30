package com.igodating.commons.security.models;

public interface JwtUserWrapper {

    JwtUser getJwtUser();

    boolean isBackendWrapper();
}
