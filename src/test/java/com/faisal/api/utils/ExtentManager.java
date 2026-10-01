package com.faisal.api.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {

    private static ExtentReports extent;

    private ExtentManager() {
        // Prevent object creation
    }

    public static ExtentReports getInstance() {

        if (extent == null) {

            ExtentSparkReporter spark =
                    new ExtentSparkReporter(
                            "test-output/ExtentReport.html"
                    );

            spark.config().setDocumentTitle(
                    "REST API Automation Report"
            );

            spark.config().setReportName(
                    "REST Assured API Regression Suite"
            );

            extent = new ExtentReports();
            extent.attachReporter(spark);

            extent.setSystemInfo(
                    "Project",
                    "REST API Automation Framework"
            );

            extent.setSystemInfo(
                    "API",
                    "JSONPlaceholder"
            );

            extent.setSystemInfo(
                    "Framework",
                    "REST Assured + TestNG"
            );

            extent.setSystemInfo(
                    "Language",
                    "Java"
            );

            extent.setSystemInfo(
                    "Java Version",
                    System.getProperty("java.version")
            );
        }

        return extent;
    }
}