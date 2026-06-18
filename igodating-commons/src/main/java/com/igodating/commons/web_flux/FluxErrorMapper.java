package com.igodating.commons.web_flux;

import com.igodating.commons.dto.ResponseWrapper;
import com.igodating.commons.exception.ApiErrorCode;
import com.igodating.commons.exception.FailedResponseException;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.ConnectException;
import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

@Log4j2
@UtilityClass
public class FluxErrorMapper {

    public Function<WebClientResponseException, FailedResponseException> mapWebClientExceptionToResponseException() {
        return e -> {
            final URI uri = Optional.ofNullable(e.getRequest()).map(HttpRequest::getURI).orElse(null);
            final HttpStatusCode statusCode = e.getStatusCode();
            final HttpMethod method = e.getRequest().getMethod();
            final HttpHeaders headers = Optional.ofNullable(e.getRequest()).map(HttpRequest::getHeaders).orElse(null);
            final String body = e.getResponseBodyAsString();
            final HttpStatusCode status;
            final ApiErrorCode errorCode;
            switch (HttpStatus.resolve(statusCode.value())) {
                case UNAUTHORIZED -> {
                    errorCode = ApiErrorCode.UNAUTHORIZED;
                    status = e.getStatusCode();
                }
                case FORBIDDEN -> {
                    errorCode = ApiErrorCode.ACCESS_DENIED;
                    status = e.getStatusCode();
                }
                case SERVICE_UNAVAILABLE -> {
                    errorCode = ApiErrorCode.API_CONNECTION_ERROR;
                    status = e.getStatusCode();
                }
                default -> {
                    status = HttpStatus.OK;
                    errorCode = ApiErrorCode.INTERNAL_SERVER_ERROR;
                }
            }
            log.error("Unexpected error while processing request: method = '{}', uri = '{}'." +
                    " Status code: '{}'." +
                    " Body: '{}'." +
                    " Headers: '{}'." +
                    " Exception message: '{}'", method, uri, statusCode, body, headers, e.getMessage());
            return new FailedResponseException(ResponseWrapper.fail(errorCode).setStatus(status), status);
        };
    }

    public Function<ConnectException, FailedResponseException> mapConnectExceptionToResponseException() {
        return e -> {
            log.error("Connect exception while processing request. Message {}", e.getMessage());
            return new FailedResponseException(ResponseWrapper.fail(ApiErrorCode.API_CONNECTION_ERROR));
        };
    }
}
