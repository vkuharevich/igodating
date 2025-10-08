package com.igodating.commons.dto;

import java.util.concurrent.atomic.AtomicReference;


public enum ServiceEnum {

    GEODATA, QUESTIONARY, USER, CHAT, UNDEFINED;
    private static final AtomicReference<ServiceEnum> CURRENT_SERVICE = new AtomicReference<>(UNDEFINED);


    public static ServiceEnum getCurrentService() {
        return CURRENT_SERVICE.get();
    }

    public static void setCurrentService(ServiceEnum e) {
        CURRENT_SERVICE.compareAndSet(UNDEFINED, e);
    }
}
