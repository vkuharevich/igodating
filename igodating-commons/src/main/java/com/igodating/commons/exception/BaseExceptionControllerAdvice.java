package com.igodating.commons.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import jakarta.validation.ValidationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class BaseExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    private MultipartProperties multipartProperties;

    protected Map<String, Object> getRequestHeaders(HttpServletRequest request) {
        final Map<String, Object> returnValue = new HashMap<>();

        final Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String headerName = headerNames.nextElement();
            returnValue.put(headerName, request.getHeader(headerName));
        }
        return returnValue;
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleException(AccessDeniedException e) {
        throw e;
    }


    protected Map<String, Object> getRequestHeaders() {
        return this.getRequestHeaders(this.request);
    }


    @ExceptionHandler(jakarta.validation.ValidationException.class)
    public ResponseEntity<Object> handleValidationException(ValidationException e) {
        log.error("Illegal access while handling api request. URL {}, Method {}, Headers {}", ServletUriComponentsBuilder.fromCurrentRequest().toUriString(), request.getMethod(), this.getRequestHeaders(), e);
        throw e;
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            return ResponseEntity.ok(ActionResult.fail(ApiErrorCode.INVALID_FORM_PARAM, null, this.fieldErrorsToMap(fieldErrors)));
        } else {
            List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
            return ResponseEntity.ok(ActionResult.fail(ApiErrorCode.INVALID_FORM_PARAM, null, this.fieldAllErrorsToMap(allErrors)));
        }
    }

    private Map<String, List<String>> fieldErrorsToMap(List<FieldError> errors) {
        Map<String, List<String>> fieldsErrors = new HashMap<>();
        for (FieldError error : errors) {
            List<String> fieldErrors = fieldsErrors.computeIfAbsent(error.getField(), s -> new ArrayList<>());
            fieldErrors.add(error.getDefaultMessage());
        }
        return fieldsErrors;
    }

    private List<String> fieldAllErrorsToMap(List<ObjectError> allErrors) {
        return allErrors.stream().map(ObjectError::getDefaultMessage).toList();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException e) {
        final Map<String, List<String>> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            for (Path.Node node : violation.getPropertyPath()) {
                if (node.getKind() == ElementKind.PARAMETER) {
                    errors.computeIfAbsent(node.getName(), (k) -> new ArrayList<>())
                            .add(violation.getMessage());
                }
            }
        }
        return ResponseEntity.ok(ActionResult.fail(ApiErrorCode.INVALID_PATH_VARIABLES, null, errors));

    }

//    @ExceptionHandler(UniqueConstraintException.class)
//    @ResponseBody
//    public ResponseEntity<Object> handleFailedActionResult(UniqueConstraintException e) {
//        log.debug("Non unique fields {}, cause {}", e.getUniqueFields(), e);
//        return ResponseEntity.ok(ActionResult.fail(ApiErrorCode.UNIQUE_CONSTRAINT, e.getUniqueFields()));
//    }


    @ExceptionHandler(FailedResponseException.class)
    @ResponseBody
    public ResponseEntity<Object> handleFailedActionResult(FailedResponseException e, ServletWebRequest webRequest) {
        webRequest.getResponse().reset();
        return ResponseEntity.ok(e.getActionResult().wrapToFail());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    public ResponseEntity<Object> entityNotFound(EntityNotFoundException ex) {
        log.debug("Not existing entity, cause", ex);
        return ResponseEntity.ok(ActionResult.fail(ApiErrorCode.ENTITY_NOT_FOUND, ex.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error("Payload size too large", ex);
        final BigDecimal limitInMb = BigDecimal.valueOf(multipartProperties.getMaxFileSize().toMegabytes());
        return new ResponseEntity<>(ActionResult.fail(ApiErrorCode.MULTIPART_PAYLOAD_TOO_LARGE,
                String.format("Payload size too large. Limit is %s MB", limitInMb)),
                status);
    }
}
