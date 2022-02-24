package com.example.demo.config;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String s, Response response) {
        Exception exception = defaultErrorDecoder.decode(s, response);

        if (exception instanceof RetryableException) {
            return exception;
        }

        if (response.status() == 503) {
            log.info("Retrying ...");
            return new RetryableException(response.status(), "503 error",
                    response.request().httpMethod(), new Date(), response.request());
        }

        return exception;
    }
}