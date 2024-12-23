package com.igodating.commons.model;

public interface Identifiable<T> {
    T getId();
    void setId(T id);
}
