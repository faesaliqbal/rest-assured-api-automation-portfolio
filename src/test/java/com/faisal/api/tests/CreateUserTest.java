package com.faisal.api.tests;

import com.faisal.api.clients.UserClient;
import com.faisal.api.models.User;
import com.faisal.api.specs.ResponseSpec;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.*;

public class CreateUserTest {

    @Test
    public void verifyCreateUser() {

        UserClient userClient = new UserClient();

        User newUser = new User(
                "Faisal",
                "faisal.qa",
                "faisal@example.com"
        );

        Response response = userClient.createUser(newUser);

        response.then().body("id", allOf(instanceOf(Integer.class), greaterThan(0)));

        response.then()
                .spec(ResponseSpec.successResponse(201));

        Assert.assertEquals(
                response.jsonPath().getString("name"),
                newUser.getName(),
                "User name is incorrect"
        );

        Assert.assertEquals(
                response.jsonPath().getString("username"),
                newUser.getUsername(),
                "Username is incorrect"
        );

        Assert.assertEquals(
                response.jsonPath().getString("email"),
                newUser.getEmail(),
                "Email is incorrect"
        );
    }

}
