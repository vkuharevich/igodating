package com.igodating.commons.exception;

import com.igodating.commons.dto.ResponseWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public class FailedResponseException extends RuntimeException{

    private final ResponseWrapper<?> responseWrapper;
    private final HttpStatusCode status;

    public FailedResponseException(ResponseWrapper<?> responseWrapper) {
        this.responseWrapper = responseWrapper;
        this.status = HttpStatus.OK;
    }
}
