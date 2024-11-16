package com.igodating.questionary.model;

public interface Identifiable<T> {
    T getId();
    void setId(T id);
}
