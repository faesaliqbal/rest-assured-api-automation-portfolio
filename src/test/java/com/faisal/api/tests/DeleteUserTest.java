package com.faisal.api.tests;

import com.faisal.api.clients.UserClient;
import io.restassured.response.Response;
import com.faisal.api.specs.ResponseSpec;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DeleteUserTest {

    @Test
    public void verifyDeleteUser() {

        UserClient userClient = new UserClient();

        Response response = userClient.deleteUser(1);
        response.then().spec(ResponseSpec.successResponse(200));
        Assert.assertTrue(response.jsonPath().getMap("$").isEmpty(),
                "Simulated delete should return an empty JSON object");
    }
}
