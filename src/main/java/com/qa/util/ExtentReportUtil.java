package com.qa.util;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.qa.pages.BaseTest;

import org.openqa.selenium.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReportUtil implements ITestListener {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();
    private static String reportPath;
    private static String screenshotsFolder;

    // Initialize the report and screenshot folder
    public static void initializeReport() {
        // Get current date and time
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String reportFolder = System.getProperty("user.dir") + "/test-output/";

        // Set the report path and screenshot folder path
        reportPath = reportFolder + "ExtentReport_" + timeStamp + ".html";
        screenshotsFolder = reportFolder + "screenshots_" + timeStamp + "/";
        new File(screenshotsFolder).mkdirs();  // Create the screenshot folder

        // Set up Extent Report
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setDocumentTitle("Automation Test Execution Report");
        sparkReporter.config().setReportName("Test Results");

        // No theme configuration to avoid issues
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        // Add system info
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("Tester", "Your Name");
    }

    // Create a test with the name and description
    public static void createTest(String testName, String description) {
        ExtentTest test = extent.createTest(testName, description);
        testThread.set(test);
    }

    // Log step with a message
    public static void logStep(String message) {
        ExtentTest test = testThread.get();
        if (test != null) {
            test.log(Status.INFO, message);
        } else {
            System.out.println("Test object is not initialized.");
        }
    }

    // Log priority step with a different status
    public static void logPriorityStep(String message) {
        ExtentTest test = testThread.get();
        if (test != null) {
            test.log(Status.PASS, "🔥 PRIORITY STEP: " + message);
        }
    }

    // Attach screenshot after each step
    public static void attachScreenshot(WebDriver driver, String stepDescription) throws IOException {
        String screenshotPath = captureScreenshot(driver, stepDescription);
        ExtentTest test = testThread.get();
        if (test != null) {
            test.addScreenCaptureFromPath(screenshotPath, stepDescription);
        }
    }

    // Capture screenshot and save to the desired folder
    private static String captureScreenshot(WebDriver driver, String stepDescription) throws IOException {
        String screenshotPath = screenshotsFolder + "/screenshot_" + stepDescription + ".png";
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Files.copy(screenshotFile.toPath(), new File(screenshotPath).toPath());
        return screenshotPath;
    }

    // Finalize the report after all tests are executed
    public static void finalizeReport() throws IOException {
        if (extent != null) {
            extent.flush();
        }
    }

    @Override
    public void onStart(ITestContext context) {
        initializeReport();
    }

    @Override
    public void onFinish(ITestContext context) {
        try {
            finalizeReport();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        createTest(result.getMethod().getMethodName(), result.getMethod().getDescription());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        testThread.get().log(Status.PASS, "Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        testThread.get().log(Status.FAIL, "Test Failed: " + result.getThrowable());
        WebDriver driver = ((BaseTest) result.getInstance()).getDriver();
        try {
            attachScreenshot(driver, result.getMethod().getMethodName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        testThread.get().log(Status.SKIP, "Test Skipped: " + result.getThrowable());
    }
}
