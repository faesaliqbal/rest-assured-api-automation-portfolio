package com.faisal.api.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Arrays;

public class TestListener implements ITestListener {

    private static final ExtentReports extent =
            ExtentManager.getInstance();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTestManager.removeTest();

        String testName = result.getMethod().getMethodName();

        // Makes DataProvider executions distinguishable
        if (result.getParameters().length > 0) {
            testName += " - " +
                    Arrays.toString(result.getParameters());
        }

        ExtentTestManager.setTest(
                extent.createTest(testName)
        );
    }

    @Override
    public void onTestSuccess(ITestResult result) {

        if (ExtentTestManager.getTest() != null) {
            ExtentTestManager.getTest()
                    .pass("API test passed successfully");
        }

        ExtentTestManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String diagnostics = ExtentTestManager.getDiagnostics();
        if (diagnostics != null) {
            System.err.println(diagnostics);
            if (ExtentTestManager.getTest() != null) {
                ExtentTestManager.getTest().info(MarkupHelper.createCodeBlock(diagnostics));
            }
        }

        if (ExtentTestManager.getTest() != null) {

            if (result.getThrowable() != null) {
                ExtentTestManager.getTest()
                        .fail(result.getThrowable());
            } else {
                ExtentTestManager.getTest()
                        .fail("API test failed");
            }
        }

        ExtentTestManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {

        if (ExtentTestManager.getTest() != null) {

            if (result.getThrowable() != null) {
                ExtentTestManager.getTest()
                        .skip(result.getThrowable());
            } else {
                ExtentTestManager.getTest()
                        .skip("API test skipped");
            }
        }

        ExtentTestManager.removeTest();
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}
