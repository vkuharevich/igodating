package com.igodating.questionary.controller.exception;

import com.igodating.commons.dto.ErrorDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Log4j2
public class QuestionaryExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handle(Exception e) {
        log.error("Internal error", e);
        return new ErrorDto(e.getMessage(), LocalDateTime.now());
    }
}
