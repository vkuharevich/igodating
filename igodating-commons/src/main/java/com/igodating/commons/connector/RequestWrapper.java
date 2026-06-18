package com.igodating.commons.connector;


import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

@Getter
@Builder
public class RequestWrapper {

    private final Object requestBody;
    private final Object requestParamsObject;
    private final MultiValueMap<String, String> requestParams;
    private final HttpHeaders httpHeaders;

    public RequestWrapper(Object requestBody,
                          Object requestParamsObject,
                          MultiValueMap<String, String> requestParams,
                          HttpHeaders httpHeaders) {
        this.requestBody = requestBody;
        this.requestParamsObject = requestParamsObject;
        this.requestParams = requestParams;
        if (httpHeaders == null) {
            this.httpHeaders = new HttpHeaders();
            this.httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        } else {
            final MediaType contentType = httpHeaders.getContentType();
            if (contentType == null) {
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            }
            this.httpHeaders = httpHeaders;
        }
    }

    /**
     * @return request wrapper with default content type application/json and without request parameters and body
     */
    public static RequestWrapper empty() {
        return new RequestWrapper(null, null, null, null);
    }
}
