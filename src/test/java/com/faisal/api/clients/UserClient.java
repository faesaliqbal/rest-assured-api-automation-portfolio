package com.faisal.api.clients;

import com.faisal.api.models.User;
import com.faisal.api.specs.RequestSpec;
import com.faisal.api.utils.ExtentTestManager;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserClient {

    // GET - Retrieve a single user
    public Response getUser(int userId) {

        String endpoint = "/users/" + userId;

        Response response = given()
                .spec(RequestSpec.getRequestSpec())
                .when()
                .get(endpoint);

        logApiDetails("GET", endpoint, response);

        return response;
    }

    // GET - Retrieve all users
    public Response getAllUsers() {

        String endpoint = "/users";

        Response response = given()
                .spec(RequestSpec.getRequestSpec())
                .when()
                .get(endpoint);

        logApiDetails("GET", endpoint, response);

        return response;
    }

    // POST - Create a user
    public Response createUser(User user) {

        String endpoint = "/users";

        Response response = given()
                .spec(RequestSpec.getRequestSpec())
                .body(user)
                .when()
                .post(endpoint);

        logApiDetails("POST", endpoint, response);

        return response;
    }

    // PUT - Update a user
    public Response updateUser(int userId, User user) {

        String endpoint = "/users/" + userId;

        Response response = given()
                .spec(RequestSpec.getRequestSpec())
                .body(user)
                .when()
                .put(endpoint);

        logApiDetails("PUT", endpoint, response);

        return response;
    }

    // DELETE - Delete a user
    public Response deleteUser(int userId) {

        String endpoint = "/users/" + userId;

        Response response = given()
                .spec(RequestSpec.getRequestSpec())
                .when()
                .delete(endpoint);

        logApiDetails("DELETE", endpoint, response);

        return response;
    }

    // Log API execution details to Extent Report
    private void logApiDetails(
            String method,
            String endpoint,
            Response response) {

        if (ExtentTestManager.getTest() != null) {

            ExtentTestManager.getTest()
                    .info("Method: " + method);

            ExtentTestManager.getTest()
                    .info("Endpoint: " + endpoint);

            ExtentTestManager.getTest()
                    .info("Status Code: " + response.statusCode());

            ExtentTestManager.getTest()
                    .info("Response Time: " + response.time() + " ms");
        }
    }
}
