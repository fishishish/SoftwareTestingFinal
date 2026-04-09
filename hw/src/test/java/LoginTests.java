import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LoginTests {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL       = "https://github.com/";
    private static final String VALID_USERNAME = "TestFinal777";
    private static final String VALID_PASSWORD = "ValidExample8!";
    private static final String WRONG_PASSWORD = "InvalidExample";
    private static final String SCREENSHOT_DIR = "screenshots/";

    // ── Pause durations (ms) — adjust to taste ────────────────────────────────
    private static final int PAUSE_SHORT  = 500;   // brief beat between actions
    private static final int PAUSE_MEDIUM = 1000;   // let the viewer read a result
    private static final int PAUSE_LONG   = 2000;   // hold on a key moment / assertion
    // ─────────────────────────────────────────────────────────────────────────

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    @BeforeClass
    public void suiteSetup() {
        startFreshIncognitoDriver();
        new File(SCREENSHOT_DIR).mkdirs();
    }

    @AfterClass
    public void suiteTeardown() {
        sleep(PAUSE_LONG);
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterMethod
    public void resetAfterLoginTest(java.lang.reflect.Method method) {
        if (method.getName().equals("test2_ValidLogin")) {
            sleep(PAUSE_LONG);      // hold on the dashboard before killing the session
            driver.quit();
            startFreshIncognitoDriver();
        }
    }

    private void startFreshIncognitoDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    private void goToLoginPage() {
        driver.get(BASE_URL);
        sleep(PAUSE_MEDIUM);        // homepage visible
        wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("Sign in"))).click();
        wait.until(ExpectedConditions.urlContains("login"));
        sleep(PAUSE_MEDIUM);        // login page loaded and readable
    }

    // =========================================================================
    // Test 1 – Navigate to login page and verify title
    // =========================================================================
    @Test(priority = 1)
    public void test1_VerifyLoginPageTitle() {
        driver.get(BASE_URL);
        sleep(PAUSE_MEDIUM);                        // viewer sees the homepage

        WebElement signInBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Sign in")));
        sleep(PAUSE_SHORT);                         // brief pause before clicking
        signInBtn.click();

        wait.until(ExpectedConditions.urlContains("login"));
        sleep(PAUSE_MEDIUM);                        // login page settled

        String pageTitle = driver.getTitle();
        System.out.println("Login page title: " + pageTitle);
        sleep(PAUSE_LONG);                          // hold so viewer can read the title

        Assert.assertTrue(pageTitle.contains("Sign in to GitHub"),
                "Expected title to contain 'Sign in to GitHub' but got: " + pageTitle);
        sleep(PAUSE_MEDIUM);                        // pause after assertion passes
    }

    // =========================================================================
    // Test 2 – Log in with valid credentials and verify landing page
    // =========================================================================
    @Test(priority = 2)
    public void test2_ValidLogin() {
        goToLoginPage();

        WebElement usernameField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("login_field")));
        sleep(PAUSE_SHORT);                         // pause before typing username
        usernameField.click();
        usernameField.sendKeys(VALID_USERNAME);
        sleep(PAUSE_MEDIUM);                        // viewer sees the typed username

        WebElement passwordField = driver.findElement(By.id("password"));
        sleep(PAUSE_SHORT);                         // pause before typing password
        passwordField.click();
        passwordField.sendKeys(VALID_PASSWORD);
        sleep(PAUSE_MEDIUM);                        // viewer sees password dots filled in

        sleep(PAUSE_SHORT);                         // brief beat before submitting
        driver.findElement(By.name("commit")).click();

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login")));
        sleep(PAUSE_LONG);                          // hold on dashboard so viewer can see it

        String pageTitle = driver.getTitle();
        System.out.println("Post-login page title: " + pageTitle);

        Assert.assertTrue(pageTitle.contains("GitHub"),
                "Expected to land on GitHub dashboard but got: " + pageTitle);
        sleep(PAUSE_MEDIUM);                        // pause after assertion passes
    }

    // =========================================================================
    // Test 3 – Log in with wrong password and verify error message
    // =========================================================================
    @Test(priority = 3)
    public void test3_InvalidPasswordShowsError() {
        goToLoginPage();

        WebElement usernameField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("login_field")));
        sleep(PAUSE_SHORT);
        usernameField.click();
        usernameField.sendKeys(VALID_USERNAME);
        sleep(PAUSE_MEDIUM);                        // viewer sees the username

        WebElement passwordField = driver.findElement(By.id("password"));
        sleep(PAUSE_SHORT);
        passwordField.click();
        passwordField.sendKeys(WRONG_PASSWORD);
        sleep(PAUSE_MEDIUM);                        // viewer sees wrong password entered

        sleep(PAUSE_SHORT);                         // brief beat before submitting
        driver.findElement(By.name("commit")).click();

        WebElement errorBanner = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".js-flash-alert")));
        sleep(PAUSE_LONG);                          // hold on the error banner

        String errorText = errorBanner.getText();
        System.out.println("Login error message: " + errorText);

        Assert.assertTrue(errorBanner.isDisplayed(),
                "Expected an error message to be displayed for wrong password.");
        sleep(PAUSE_MEDIUM);                        // pause after assertion passes
    }

    // =========================================================================
    // Test 4 – Forgot password link navigates to reset page; take screenshot
    // =========================================================================
    @Test(priority = 4)
    public void test4_ForgotPasswordLink() throws IOException {
        goToLoginPage();

        WebElement forgotLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.linkText("Forgot password?")));
        sleep(PAUSE_SHORT);                         // pause before clicking
        forgotLink.click();

        wait.until(ExpectedConditions.urlContains("password_reset"));
        sleep(PAUSE_LONG);                          // hold on the reset page

        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, "https://github.com/password_reset",
                "Expected URL to be https://github.com/password_reset but got: " + currentUrl);
        sleep(PAUSE_MEDIUM);                        // pause after URL assertion passes

        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Path destination = Paths.get(SCREENSHOT_DIR + "test4_forgot_password.png");
        Files.copy(screenshot.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Screenshot saved to: " + destination.toAbsolutePath());
        sleep(PAUSE_MEDIUM);                        // brief hold after screenshot taken
    }

    // =========================================================================
    // Test 5 – "Continue with Google" button navigates to Google auth page
    // =========================================================================
    @Test(priority = 5)
    public void test5_SignInWithGoogle() {
        goToLoginPage();

        WebElement googleSignInBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[data-disable-with='Continuing with Google...']")));
        sleep(PAUSE_SHORT);                         // pause before clicking Google button
        googleSignInBtn.click();

        sleep(PAUSE_LONG);                          // hold on Google OAuth page

        String pageTitle = driver.getTitle();
        System.out.println("Google sign-in redirect page title: " + pageTitle);

        Assert.assertTrue(
                pageTitle.toLowerCase().contains("google") ||
                driver.getCurrentUrl().contains("accounts.google.com"),
                "Expected Google OAuth page but got title: " + pageTitle +
                " at URL: " + driver.getCurrentUrl());
        sleep(PAUSE_MEDIUM);                        // pause after assertion passes
    }
}