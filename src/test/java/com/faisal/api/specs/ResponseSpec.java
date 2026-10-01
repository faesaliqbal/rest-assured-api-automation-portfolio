package com.faisal.api.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;

public class ResponseSpec {

    private ResponseSpec() {
        // Prevent object creation
    }

    public static ResponseSpecification successResponse(int statusCode) {

        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .expectContentType(ContentType.JSON)
                .build();
    }
}