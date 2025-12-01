package com.igodating.commons.exception;

import com.igodating.commons.dto.ResponseWrapper;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class FailedResponseWrapperException extends RuntimeException{

    private final ResponseWrapper<?> actionResult;
    private final HttpStatusCode status;

    public FailedResponseWrapperException(ResponseWrapper<?> actionResult) {
        this.actionResult = actionResult;
        this.status = HttpStatus.OK;
    }
}
