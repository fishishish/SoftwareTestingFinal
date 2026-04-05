import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SignupTests {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL   = "https://github.com/";
    private static final String SIGNUP_URL = "https://github.com/signup";
    private static final String TOS_URL    =
            "https://docs.github.com/en/site-policy/github-terms/github-terms-of-service";

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
    }

    @AfterClass
    public void suiteTeardown() {
        sleep(PAUSE_LONG);
        if (driver != null) {
            driver.quit();
        }
    }

    private void startFreshIncognitoDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ── Helper: navigate to the sign-up page ─────────────────────────────────
    private void goToSignUpPage() {
        driver.get(SIGNUP_URL);
        wait.until(ExpectedConditions.urlContains("signup"));
    }

    // =========================================================================
    // Test 1 – Navigate to sign-up page from home and verify title
    // =========================================================================
    @Test(priority = 1)
    public void test1_VerifySignUpPageTitle() {
        driver.get(BASE_URL);

        WebElement signUpBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.linkText("Sign up")));
        signUpBtn.click();

        wait.until(ExpectedConditions.urlContains("signup"));

        String pageTitle = driver.getTitle();
        System.out.println("Sign-up page title: " + pageTitle);

        // Assert current page title matches sign up page title
        Assert.assertTrue(pageTitle.contains("Sign up for Github") || pageTitle.contains("Sign up"),
                "Expected a sign-up related title but got: " + pageTitle);
        
        sleep(PAUSE_MEDIUM); 
    }

    // =========================================================================
    // Test 2 – Submit empty form, assert missing-field validation messages
    // =========================================================================
    @Test(priority = 2)
    public void test2_EmptyFormValidation() {
        goToSignUpPage();

        WebElement createAccountBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[.//span[normalize-space()='Create account']]")));
        createAccountBtn.click();

        // GitHub renders per-field error messages; collect them all
        List<WebElement> errorMessages = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.xpath("//div[contains(@class,'error')]//p[contains(@class,'nux-error')]")));

        Assert.assertFalse(errorMessages.isEmpty(),
                "Expected validation error messages to be present.");

        System.out.println("Validation error messages found (" + errorMessages.size() + "):");
        for (WebElement msg : errorMessages) {
            System.out.println("  - " + msg.getText());

        sleep(PAUSE_MEDIUM); 

        }
    }

    // =========================================================================
    // Test 3 – Select "United States" from the country dropdown
    // =========================================================================
    @Test(priority = 3)
    public void test3_CountryDropdown() {
        goToSignUpPage();

        // Step 1 – open the dropdown
        WebElement countryDropdown = wait.until(
        ExpectedConditions.presenceOfElementLocated(
                By.id("country-dropdown-panel-button")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", countryDropdown);
        sleep(PAUSE_SHORT);
        countryDropdown.click();

        sleep(PAUSE_MEDIUM); 

        // Step 2 – select Canada
        WebElement caOption = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[data-value='CA']")));
        caOption.click();

        sleep(PAUSE_MEDIUM); 

        // Step 3 – assert the selection
        String selectedText = countryDropdown.getText();
        Assert.assertTrue(selectedText.contains("Canada"),
                "Expected 'Canada' to be selected but got: " + selectedText);

        System.out.println("Country selected: " + selectedText);

        sleep(PAUSE_MEDIUM); 
    }

    // =========================================================================
    // Test 4 – Enter invalid email and assert the error message
    // =========================================================================
    @Test(priority = 4)
    public void test4_InvalidEmailError() {
        goToSignUpPage();

        WebElement emailField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("email")));
        emailField.click();
        emailField.sendKeys("notavalidemail");

        // Trigger validation by tabbing away or clicking Continue
        emailField.sendKeys(Keys.TAB);

        sleep(PAUSE_MEDIUM); 

        // Wait for the inline error to appear
        WebElement emailError = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//p[contains(.,'Email is invalid')]")));

        String errorText = emailError.getText();
        System.out.println("Email validation error: " + errorText);

        Assert.assertTrue(errorText.toLowerCase().contains("Email is invalid") ||
                          errorText.toLowerCase().contains("already taken"),
                "Expected error 'Email is invalid or already taken' but got: " + errorText);
    }

    // =========================================================================
    // Test 5 – Terms of Service link opens correct URL; close the tab
    // =========================================================================
    @Test(priority = 5)
    public void test5_TermsOfServiceLink() {
        goToSignUpPage();


        WebElement tosLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.partialLinkText("Terms")));
        tosLink.click();

        // Switch to the new tab
        String originalHandle = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(originalHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        // Wait for the ToS page to load
        wait.until(ExpectedConditions.urlContains("terms-of-service"));

        String currentUrl = driver.getCurrentUrl();
        System.out.println("Terms of Service URL: " + currentUrl);

        Assert.assertEquals(currentUrl, TOS_URL,
                "Expected ToS URL " + TOS_URL + " but got: " + currentUrl);

        sleep(PAUSE_MEDIUM); 

        // Close the new tab and return to the original
        driver.close();
        driver.switchTo().window(originalHandle);
    }
}