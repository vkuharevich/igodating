package com.igodating.commons.model;

public interface SoftDeletable {
    void setToDelete();
    boolean isDeleted();
}
