package com.igodating.commons.connector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igodating.commons.dto.ResponseWrapper;
import com.igodating.commons.exception.FailedResponseException;
import com.igodating.commons.utils.ParameterizedTypeImpl;
import com.igodating.commons.utils.UrlParamsUtils;
import com.igodating.commons.web_flux.FluxErrorMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.URI;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class BaseConnector {

    protected final WebClient webClient;
    private final String baseUrl;
    private final JsonMapper mapper;

    public <T> Mono<T> exchange(String relativeUrl, HttpMethod method, Class<T> responseClass, @NotNull RequestWrapper requestWrapper, Object... uriVariables) {
        final URI uri = this.createUri(relativeUrl, requestWrapper.getRequestParamsObject(), uriVariables);
        return
                webClient.method(method)
                        .uri(uri)
                        .headers(headers -> headers.addAll(requestWrapper.getHttpHeaders()))
                        .bodyValue(requestWrapper.getRequestBody() != null ? requestWrapper.getRequestBody() : "")
                        .retrieve()
                        .bodyToMono(responseClass)
                        .onErrorMap(WebClientResponseException.class, FluxErrorMapper.mapWebClientExceptionToResponseException())
                        .onErrorMap(ConnectException.class, FluxErrorMapper.mapConnectExceptionToResponseException());
    }

    protected <T> Mono<ResponseWrapper<T>> getAction(String relativeUrl, Class<T> responseType, @NotNull RequestWrapper requestWrapper, Object... uriVariables) {
        return this.action(relativeUrl, HttpMethod.GET, responseType, null, requestWrapper.getRequestParamsObject(), requestWrapper.getHttpHeaders(), uriVariables);
    }

    protected <T> Mono<ResponseWrapper<T>> getAction(String relativeUrl, TypeReference<T> responseType, @NotNull RequestWrapper requestWrapper, Object... uriVariables) {
        return this.action(relativeUrl, HttpMethod.GET, responseType.getType(), null, requestWrapper.getRequestParamsObject(), requestWrapper.getHttpHeaders(), uriVariables);
    }

    protected <T> Mono<ResponseWrapper<T>> putAction(String relativeUrl, Class<T> responseType, @NotNull RequestWrapper requestWrapper, Object... uriVariables) {
        return this.action(relativeUrl, HttpMethod.PUT, responseType, requestWrapper.getRequestBody(), requestWrapper.getRequestParamsObject(), requestWrapper.getHttpHeaders(), uriVariables);
    }

    protected <T> Mono<ResponseWrapper<T>> putAction(String relativeUrl, TypeReference<T> responseType, @NotNull RequestWrapper requestWrapper, Object... uriVariables) {
        return this.action(relativeUrl, HttpMethod.PUT, responseType.getType(), requestWrapper.getRequestBody(), requestWrapper.getRequestParamsObject(), requestWrapper.getHttpHeaders(), uriVariables);
    }


    protected <T> Mono<ResponseWrapper<T>> patchAction(String relativeUrl, Class<T> responseType, @NotNull RequestWrapper requestWrapper, Object... uriVariables) {
        return this.action(relativeUrl, HttpMethod.PATCH, responseType, requestWrapper.getRequestBody(), requestWrapper.getRequestParamsObject(), requestWrapper.getHttpHeaders(), uriVariables);
    }

    protected <T> Mono<ResponseWrapper<T>> patchAction(String relativeUrl, TypeReference<T> responseType, @NotNull RequestWrapper requestWrapper, Object... uriVariables) {
        return this.action(relativeUrl, HttpMethod.PATCH, responseType.getType(), requestWrapper.getRequestBody(), requestWrapper.getRequestParamsObject(), requestWrapper.getHttpHeaders(), uriVariables);
    }

    protected <T> Mono<ResponseWrapper<T>> deleteAction(String relativeUrl, Class<T> responseType, @NotNull RequestWrapper requestWrapper, Object... uriVariables) {
        return this.action(relativeUrl, HttpMethod.DELETE, responseType, requestWrapper.getRequestBody(), requestWrapper.getRequestParamsObject(), requestWrapper.getHttpHeaders(), uriVariables);
    }

    protected <T> Mono<ResponseWrapper<T>> deleteAction(String relativeUrl, TypeReference<T> responseType, @NotNull RequestWrapper requestWrapper, Object... uriVariables) {
        return this.action(relativeUrl, HttpMethod.DELETE, responseType.getType(), requestWrapper.getRequestBody(), requestWrapper.getRequestParamsObject(), requestWrapper.getHttpHeaders(), uriVariables);
    }

    protected <T> Mono<ResponseWrapper<T>> postAction(String relativeUrl, Class<T> responseType, @NotNull RequestWrapper requestWrapper, Object... uriVariables) {
        return this.action(relativeUrl, HttpMethod.POST, responseType, requestWrapper.getRequestBody(), requestWrapper.getRequestParamsObject(), requestWrapper.getHttpHeaders(), uriVariables);
    }

    protected <T> Mono<ResponseWrapper<T>> postAction(String relativeUrl, TypeReference<T> responseType, @NotNull RequestWrapper requestWrapper, Object... uriVariables) {
        return this.action(relativeUrl, HttpMethod.POST, responseType.getType(), requestWrapper.getRequestBody(), requestWrapper.getRequestParamsObject(), requestWrapper.getHttpHeaders(), uriVariables);
    }

    @SneakyThrows
    private <T> Mono<ResponseWrapper<T>> action(String uriPath,
                                                HttpMethod method,
                                                @NotNull Type type,
                                                @Nullable Object requestBody,
                                                @Nullable Object urlParamsObject,
                                                HttpHeaders httpHeaders,
                                                Object... uriVariables) {
        final URI uri = this.createUri(uriPath, urlParamsObject, uriVariables);
        final ParameterizedTypeReference<ResponseWrapper<T>> responseType = this.makeParametrizedType(type);
        return
                webClient.method(method)
                        .uri(uri)
                        .headers(headers -> headers.addAll(httpHeaders))
                        .bodyValue(requestBody != null ? requestBody : "")
                        .retrieve()
                        .bodyToMono(responseType)
                        .onErrorMap(WebClientResponseException.class, FluxErrorMapper.mapWebClientExceptionToResponseException())
                        .onErrorMap(ConnectException.class, FluxErrorMapper.mapConnectExceptionToResponseException())
                        .onErrorResume(FailedResponseException.class, x -> Mono.just(x.getResponseWrapper().wrapToFail()))
                        .map(x -> {
                            if (x.isError()) {
                                log.error("""
                                                Failed response from {}.
                                                Request URL - {},
                                                Request Method - {},
                                                Request body - {}.
                                                Error code {},
                                                error message {}
                                                response value {}""", this.getClass().getSimpleName(), uri, method,
                                        this.bodyAsString(httpHeaders.getContentType(), requestBody), x.getErrorCode(),
                                        x.getMessage(), this.bodyAsString(null, x.getErrorValue()));
                            }
                            return x;
                        });
    }

    private <T> ParameterizedTypeReference<ResponseWrapper<T>> makeParametrizedType(Type type) {
        return ParameterizedTypeReference.forType(new ParameterizedTypeImpl(ResponseWrapper.class, type));
    }

    @SneakyThrows
    private String bodyAsString(MediaType mediaType, Object requestBody) {
        if (requestBody != null) {
            String bodyVal;
            if (MediaType.MULTIPART_FORM_DATA.equals(mediaType)) {
                bodyVal = this.mapper.writeValueAsString(((Map<?, ?>) requestBody).get("request"));
            } else {
                bodyVal = this.mapper.writeValueAsString(requestBody);
            }
            return bodyVal;
        }
        return null;
    }

    protected URI createUri(String relativeUrl, @Nullable Object params, Object... uriVariables) {
        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(this.baseUrl + relativeUrl);
        if (params != null) {
            builder.queryParams(UrlParamsUtils.toUrlParams(params));
        }

        return encodeStrict(builder.encode().build(uriVariables));
    }

    //todo remove, see https://github.com/spring-projects/spring-framework/issues/26966
    private URI encodeStrict(URI uri) {
        String strictlyEscapedQuery = StringUtils.replace(uri.getRawQuery(), "+", "%2B");
        return UriComponentsBuilder.fromUri(uri)
                .replaceQuery(strictlyEscapedQuery)
                .build(true)
                .toUri();
    }
}
