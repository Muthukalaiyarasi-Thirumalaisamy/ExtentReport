package com.qa.Test;

import com.qa.pages.BaseTest;
import com.qa.util.ExtentReportUtil;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SampleTest extends BaseTest {

    @Test(description = "Test Home Page Navigation")
    public void testHomePageNavigation() {
        // Log the step of navigating to the homepage
        ExtentReportUtil.logStep("Navigating to the homepage");
        
        // Open the desired URL
        driver.get("https://example.com");

        // Log a priority step
        ExtentReportUtil.logPriorityStep("Verifying homepage elements");

        // Simulate taking a screenshot of the step
        try {
            ExtentReportUtil.attachScreenshot(driver, "homepage_navigation");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Example verification (use actual verification as needed)
        String pageTitle = driver.getTitle();
        Assert.assertTrue(pageTitle.contains("Example Domain"), "Title does not match expected value");
    }

    @Test(description = "Simulate Failure Scenario")
    public void testFailureScenario() {
        // Log the step
        ExtentReportUtil.logStep("Simulating failure scenario");

        // Simulate a failure (this will fail the test)
        try {
            ExtentReportUtil.attachScreenshot(driver, "failure_scenario");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // This will force the test to fail
        Assert.assertFalse(true, "Simulated failure");  
    }
}
