package com.faisal.api.utils;

import org.testng.annotations.DataProvider;

public class TestDataProvider {

    private TestDataProvider() {
        // Prevent object creation
    }

    @DataProvider(name = "userData")
    public static Object[][] userData() {

        return new Object[][]{
                {1, "Leanne Graham", "Bret", "Sincere@april.biz"},
                {2, "Ervin Howell", "Antonette", "Shanna@melissa.tv"},
                {3, "Clementine Bauch", "Samantha", "Nathan@yesenia.net"}
        };
    }
}