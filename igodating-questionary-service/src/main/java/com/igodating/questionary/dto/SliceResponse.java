package com.igodating.questionary.dto;

import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
public class SliceResponse<T> {
    
    private final List<T> content;
    private final Integer size;
    private final boolean hasNext;
    
    public SliceResponse(Slice<T> slice) {
        this.content = slice.getContent();
        this.size = slice.getSize();
        this.hasNext = slice.hasNext();
    }
}
