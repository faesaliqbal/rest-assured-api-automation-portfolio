package com.faisal.api.utils;

import com.aventstack.extentreports.ExtentTest;

public class ExtentTestManager {

    private static final ThreadLocal<ExtentTest> extentTest =
            new ThreadLocal<>();

    private static final ThreadLocal<String> diagnostics = new ThreadLocal<>();

    public static void setDiagnostics(String details) { diagnostics.set(details); }
    public static String getDiagnostics() { return diagnostics.get(); }

    private ExtentTestManager() {
    }

    public static void setTest(ExtentTest test) {
        extentTest.set(test);
    }

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    public static void removeTest() {
        extentTest.remove();
        diagnostics.remove();
    }
}
