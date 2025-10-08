package com.igodating.commons.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@Log4j2
@Schema(
        description = "Wrapper for API response",
        discriminatorProperty = "success"
)
public class ActionResult<T> {
    @Schema(title = "Response main value", nullable = true, description = "Only if success is true")
    protected T value;
    @Schema(title = "Success or not", requiredMode = Schema.RequiredMode.REQUIRED)
    protected boolean success;
    @Schema(title = "Response from microservice")
    protected ServiceEnum service;
    @Schema(title = "Error code")
    protected String errorCode;
    @Schema(title = "Detailed description of error")
    protected String message;
    @Schema(title = "Error value if present")
    protected Object errorValue;
    @Schema(title = "Target URL for redirect after response")
    protected String targetUrl;
    @JsonIgnore
    private HttpStatusCode status = HttpStatus.OK;

    public static <T> ActionResult<T> ok() {
        return ok(null, null);
    }

    public static <T> ActionResult<T> okWithRedirect(String redirectUrl) {
        return ok(null, redirectUrl);
    }

    public static <T> ActionResult<T> ok(T value) {
        return ok(value, null);
    }

    public static <T> ActionResult<T> ok(T value, String redirectUrl) {
        return new ActionResult<T>()
                .setSuccess(true)
                .setValue(value)
                .setTargetUrl(redirectUrl);
    }

    public static <T> ActionResult<T> fail(ApiErrorCode errorCode, Object errorValue) {
        return new ActionResult<T>()
                .setSuccess(false)
                .setErrorCode(errorCode.name())
                .setErrorValue(errorValue);
    }

    public static <T> ActionResult<T> fail() {
        return fail((String) null, null, null);
    }

    public static <T> ActionResult<T> fail(ApiErrorCode errorCode) {
        return fail(errorCode, null, null);
    }

    public static <T> ActionResult<T> fail(String errorCode) {
        return fail(errorCode, null, null);
    }

    public static <T> ActionResult<T> fail(String errorCode, String message) {
        return fail(errorCode, message, null);
    }

    public static <T> ActionResult<T> fail(ApiErrorCode errorCode, String message) {
        return fail(errorCode.name(), message, null);
    }

    public static <T> ActionResult<T> fail(ApiErrorCode errorCode, String message, Object errorValue) {
        return fail(errorCode.name(), message, errorValue);
    }

    public static <T> ActionResult<T> fail(String errorCode, String message, Object errorValue) {
        return new ActionResult<T>()
                .setErrorCode(errorCode)
                .setMessage(message)
                .setSuccess(false)
                .setService(ServiceEnum.getCurrentService())
                .setErrorValue(errorValue);
    }

    public T orElseThrow() {
        return this.orElseThrow(false);
    }

    public void orElseLog() {
        if (this.isError())
            log.error("Failed response, {}", this.toString());
    }

    @JsonIgnore
    public T orElseThrow(boolean propagate) {
        if (this.isError()) {
            throw new FailedActionResultException(this, propagate);
        }
        return value;
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (this.isError()) {
            throw exceptionSupplier.get();
        }
        return value;
    }

    public <U> ActionResult<U> map(Function<? super T, ? extends U> mapper) {
        if (this.isError())
            return this.wrapToFail();
        else {
            return ActionResult.ok(mapper.apply(value));
        }
    }

    public ActionResult<T> filter(Predicate<? super T> predicate) {
        if (this.isError())
            return this.wrapToFail();
        else {
            if (predicate.test(this.getValue())) {
                return this;
            } else {
                return ActionResult.fail(ApiErrorCode.NOT_FOUND);
            }
        }
    }

    @JsonIgnore
    public boolean isError() {
        return !success;
    }

    public T orElseThrowInternalRestException() {
        if (success) {
            return value;
        } else {
            throw InternalRestServiceException.builder()
                    .forward(true)
                    .view("/error")
                    .httpStatus(HttpStatus.SERVICE_UNAVAILABLE)
                    .build();
        }
    }

    public boolean hasErrorCode(ApiErrorCode... errorCodes) {
        for (ApiErrorCode apiErrorCode : errorCodes) {
            if (apiErrorCode.name().equals(errorCode)) {
                return true;
            }
        }
        return false;
    }

    public <X extends Throwable> T orElseThrow(Function<? super ActionResult<T>, ? extends X> exceptionSupplier) throws X {
        if (success) {
            return value;
        } else {
            throw exceptionSupplier.apply(this);
        }
    }

    public ActionResult<T> orElseGetAction(Function<? super ActionResult<T>, ? extends ActionResult<T>> other) {
        return success ? this : other.apply(this);
    }

    public T orElseGet(Function<? super ActionResult<T>, ? extends T> other) {
        return success ? value : other.apply(this);
    }

    public T orElse(T another) {
        if (success) {
            return value;
        }
        return another;
    }

    @SuppressWarnings("unchecked")
    public <R> ActionResult<R> wrapToFail() {
        return (ActionResult<R>) this;
    }

    public <R> ActionResult<R> wrapToFail(boolean setService) {
        return this.setService(ServiceEnum.getCurrentService()).wrapToFail();
    }
}
