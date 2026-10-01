package com.faisal.api.specs;

import com.faisal.api.utils.ConfigReader;
import com.faisal.api.utils.ExtentTestManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public final class RequestSpec {
    private RequestSpec() {}

    @SuppressWarnings("deprecation") // REST Assured 5 uses the legacy Apache HTTP client API.
    public static RequestSpecification getRequestSpec() {
        ByteArrayOutputStream diagnostics = new ByteArrayOutputStream();
        PrintStream log = new PrintStream(diagnostics, true, StandardCharsets.UTF_8);
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.baseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setConfig(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                        .httpClientFactory(() -> {
                            DefaultHttpClient client = new DefaultHttpClient();
                            client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
                            return client;
                        })
                        .setParam("http.connection.timeout", ConfigReader.positiveInt("connection.timeout.ms"))
                        .setParam("http.socket.timeout", ConfigReader.positiveInt("socket.timeout.ms"))))
                // Buffer request/response details; the listener emits them only on test failure.
                .addFilter((request, response, context) -> {
                    try {
                        return context.next(request, response);
                    } finally {
                        ExtentTestManager.setDiagnostics(diagnostics.toString(StandardCharsets.UTF_8));
                        log.close();
                    }
                })
                .addFilter(new RequestLoggingFilter(log))
                .addFilter(new ResponseLoggingFilter(log))
                .build();
    }
}
