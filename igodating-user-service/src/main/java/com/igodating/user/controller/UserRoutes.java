package com.igodating.user.controller;

public interface UserRoutes {

    String ROOT = "/rest/api";
    String USERS = ROOT + "users";
    String USER_BY_ID = USERS + "/{id:\\d+}";
}
