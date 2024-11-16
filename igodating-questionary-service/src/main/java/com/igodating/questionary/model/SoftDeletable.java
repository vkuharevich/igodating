package com.igodating.questionary.model;

public interface SoftDeletable {
    void setToDelete();
    boolean isDeleted();
}
