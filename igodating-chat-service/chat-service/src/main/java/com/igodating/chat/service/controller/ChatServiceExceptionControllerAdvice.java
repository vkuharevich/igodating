package com.igodating.chat.service.controller;

import com.igodating.chat.service.exception.ChatMemberCreateException;
import com.igodating.chat.service.exception.CheckChatRestrictionException;
import com.igodating.chat.service.exception.UserNotAuthorizedException;
import com.igodating.commons.dto.ResponseWrapper;
import com.igodating.commons.exception.ApiErrorCode;
import com.igodating.commons.exception.BaseExceptionControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestControllerAdvice
@Log4j2
public class ChatServiceExceptionControllerAdvice extends BaseExceptionControllerAdvice {


    @ExceptionHandler(Exception.class)
    public ResponseWrapper<Void> handleException(Exception e, ServletWebRequest webRequest) {
        final HttpServletRequest request = webRequest.getRequest();
        log.error("Error occurred while handling api request. URL {}, Method {}, Headers {}, principal {}",
                ServletUriComponentsBuilder.fromRequest(webRequest.getRequest()).toUriString(), request.getMethod(), this.getRequestHeaders(request), this.request.getUserPrincipal(), e);
        return ResponseWrapper.fail(ApiErrorCode.INTERNAL_SERVER_ERROR, e.getStackTrace());
    }

    @ExceptionHandler(ChatMemberCreateException.class)
    public ResponseWrapper<Void> handleChatMemberCreateException(ChatMemberCreateException e) {
        return ResponseWrapper.fail(ApiErrorCode.CHAT_MEMBER_CREATE, e.getMessage());
    }

    @ExceptionHandler(UserNotAuthorizedException.class)
    public ResponseWrapper<Void> handleUserNotAuthorizedException(UserNotAuthorizedException e) {
        final ResponseWrapper<Void> returnValue = ResponseWrapper.fail(ApiErrorCode.UNAUTHORIZED, e.getMessage());
        returnValue.setStatus(HttpStatus.UNAUTHORIZED);
        return returnValue;
    }

    @ExceptionHandler(CheckChatRestrictionException.class)
    public ResponseWrapper<List<String>> handleCheckChatRestrictionException(CheckChatRestrictionException e) {
        return ResponseWrapper.fail(ApiErrorCode.NOT_PASSED_CHAT_RESTRICTIONS, e.getMessage(), e.getRestrictionNotPassedMessages());
    }
}
