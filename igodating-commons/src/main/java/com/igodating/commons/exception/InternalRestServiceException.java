package com.igodating.commons.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class InternalRestServiceException extends RuntimeException {
    private final String view;
    private final boolean forward;
    private final boolean redirect;
    private final HttpStatus httpStatus;
    private final String message;
}
