package com.faisal.api.tests;

import com.faisal.api.clients.UserClient;
import com.faisal.api.models.User;
import com.faisal.api.specs.ResponseSpec;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import static org.hamcrest.Matchers.*;

public class UpdateUserTest {

    @Test
    public void verifyUpdateUser() {

        UserClient userClient = new UserClient();

        User updatedUser = new User(
                "Faisal Updated",
                "faisal.updated",
                "faisal.updated@example.com");

        Response response = userClient.updateUser(1, updatedUser);

        response.then().body("id", allOf(instanceOf(Integer.class), equalTo(1)));

        response.then()
                .spec(ResponseSpec.successResponse(200));

        Assert.assertEquals(
                response.jsonPath().getString("name"),
                updatedUser.getName(),
                "Updated name is incorrect");

        Assert.assertEquals(
                response.jsonPath().getString("username"),
                updatedUser.getUsername(),
                "Updated username is incorrect");

        Assert.assertEquals(
                response.jsonPath().getString("email"),
                updatedUser.getEmail(),
                "Updated email is incorrect");
    }
}
