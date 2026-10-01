package com.faisal.api.tests;

import com.faisal.api.clients.UserClient;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.MatcherAssert.assertThat;

import com.faisal.api.specs.ResponseSpec;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import com.faisal.api.utils.TestDataProvider;

public class GetUserTest {

    @Test
    public void verifyGetUser() {

        UserClient userClient = new UserClient();

        Response response = userClient.getUser(1);

        response.then()
                .body(matchesJsonSchemaInClasspath("schemas/user-schema.json"));

        response.then()
                .spec(ResponseSpec.successResponse(200));

        Assert.assertEquals(
                response.jsonPath().getInt("id"),
                1,
                "User ID is incorrect");

        Assert.assertEquals(
                response.jsonPath().getString("name"),
                "Leanne Graham",
                "User name is incorrect");

        Assert.assertEquals(
                response.jsonPath().getString("username"),
                "Bret",
                "Username is incorrect");

        Assert.assertEquals(
                response.jsonPath().getString("email"),
                "Sincere@april.biz",
                "Email is incorrect");
    }

    @Test
    public void verifyUserNotFound() {

        UserClient userClient = new UserClient();

        Response response = userClient.getUser(9999);
        response.then().spec(ResponseSpec.successResponse(404));
        Assert.assertTrue(response.jsonPath().getMap("$").isEmpty(),
                "Nonexistent user should return an empty JSON object");
    }

    @Test(dataProvider = "userData", dataProviderClass = TestDataProvider.class)
    public void verifyMultipleUsers(
            int userId,
            String expectedName,
            String expectedUsername,
            String expectedEmail) {

        UserClient userClient = new UserClient();

        Response response = userClient.getUser(userId);
        response.then().body(matchesJsonSchemaInClasspath("schemas/user-schema.json"));

        response.then()
                .spec(ResponseSpec.successResponse(200));

        Assert.assertEquals(
                response.jsonPath().getInt("id"),
                userId,
                "User ID is incorrect");

        Assert.assertEquals(
                response.jsonPath().getString("name"),
                expectedName,
                "User name is incorrect");

        Assert.assertEquals(
                response.jsonPath().getString("username"),
                expectedUsername,
                "Username is incorrect");

        Assert.assertEquals(
                response.jsonPath().getString("email"),
                expectedEmail,
                "Email is incorrect");
    }
    @Test
    public void verifyGetAllUsers() {
        Response response = new UserClient().getAllUsers();
        response.then().spec(ResponseSpec.successResponse(200));
        List<Map<String, Object>> users = response.jsonPath().getList("$");
        Assert.assertEquals(users.size(), 10, "Expected JSONPlaceholder's ten user fixtures");
        List<Integer> ids = response.jsonPath().getList("id", Integer.class);
        Assert.assertEquals(new HashSet<>(ids).size(), users.size(), "User IDs must be unique");
        Assert.assertTrue(ids.stream().allMatch(id -> id != null && id > 0), "User IDs must be positive");
        ObjectMapper mapper = new ObjectMapper();
        for (Map<String, Object> user : users) {
            String json = mapper.valueToTree(user).toString();
            assertThat("Invalid user structure for ID " + user.get("id"), json,
                    matchesJsonSchemaInClasspath("schemas/user-schema.json"));
        }
        Assert.assertTrue(ids.containsAll(List.of(1, 2, 3)), "Known users must be present");
    }
}
