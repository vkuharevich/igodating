package com.igodating.commons.security;

public interface JwtConstants {

    String REFRESH_TOKEN = "rt";
    String REFRESH_LIFETIME = "rtexp";
    String USER = "user";
    String AUTHORITIES = "authorities";
    String BACKEND_AUTHORITY = "BACKEND";

    String HEADER_NEW_TOKEN = "Access-Token";
    String HEADER_BACKEND_AUTHORIZATION = "Backend-Authorization";
}
