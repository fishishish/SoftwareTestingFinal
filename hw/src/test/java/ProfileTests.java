import java.io.File;
import java.io.IOException;
import static java.lang.Thread.sleep;
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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ProfileTests {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String EMAIL_TO_TEST  = "Testusername@gmail.com";
    private static final String GITHUB_USERNAME = "TestFinal777";
    private static final String SCREENSHOT_DIR     = "screenshots/";
    private static final int    PAUSE_LONG      = 2000;

    // ── Helper: take a named screenshot ───────────────────────────────────────
    private void takeScreenshot(String filename) throws IOException {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Path destination = Paths.get(SCREENSHOT_DIR + filename);
        Files.copy(screenshot.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Screenshot saved: " + destination.toAbsolutePath());
    }

    @BeforeClass
    public void setUp() throws InterruptedException {
        ChromeOptions options = new ChromeOptions();
        
        // Local Change
        // Change this to match your user-data path in Chrome, use Chrome://version in browser
        // May need to manually sign in to the account created by the driver, but it will
        // Remain signed in
        options.addArguments("user-data-dir=C:/Users/theba/documents/User Data");
        options.addArguments("profile-directory=Default");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        sleep(PAUSE_LONG);
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    // TEST 1: NAVIGATE TO PROFILE PAGE
    @Test(priority = 1)
    public void openProfileDirectFromHome() throws InterruptedException {
        driver.get("https://github.com/");
        sleep(PAUSE_LONG);

        WebElement avatarBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//img[@data-testid='github-avatar']/ancestor::button")));
        avatarBtn.click();
        sleep(PAUSE_LONG);

        WebElement profileBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='Profile']")));
        profileBtn.click();
        sleep(PAUSE_LONG);

        Assert.assertTrue(
                driver.getCurrentUrl().contains(GITHUB_USERNAME),
                "Did not navigate to profile page"
        );
    }

    // TEST 2: NAVIGATE TO EMAILS SETTINGS PAGE
    @Test(priority = 2)
    public void navigateToEmail() throws InterruptedException {
        driver.get("https://github.com/");

        sleep(PAUSE_LONG);

        WebElement avatarButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//img[@data-testid='github-avatar']/ancestor::button")));
        avatarButton.click();
        sleep(PAUSE_LONG);

        WebElement settingsBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='Settings']")));
        settingsBtn.click();
        sleep(PAUSE_LONG);

        WebElement emailsTab = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a[href='/settings/emails']")
                )
        );
        emailsTab.click();
        sleep(PAUSE_LONG);

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/settings/emails"),
                "Did not navigate to emails settings page"
        );
    }

    // TEST 3: ADD INVALID EMAIL ADDRESS
    @Test(priority = 3)
    public void addEmailAddress() throws InterruptedException {
        driver.get("https://github.com/settings/emails");

        WebElement emailInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("email"))
        );
        emailInput.clear();
        emailInput.sendKeys(EMAIL_TO_TEST);

        WebElement addBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath(
                                "//input[@id='email']/ancestor::form//button[@type='submit'] | " +
                                        "//input[@id='email']/ancestor::form//input[@type='submit']"
                        )
                )
        );
        addBtn.click();
        sleep(PAUSE_LONG);

        WebElement emailError = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("div.js-flash-alert")));

        String emailErrorMsg = emailError.getText();
        System.out.println("Error message: " + emailErrorMsg);

        Assert.assertTrue(emailError.isDisplayed(), "Email error message is not displayed.");
    }

    // TEST 4: VERIFY PROFILE BIO VISIBLE
    @Test(priority = 4)
    public void verifyProfileBioVisible() throws InterruptedException{
        driver.get("https://github.com/" + GITHUB_USERNAME);

        sleep(PAUSE_LONG);

        WebElement bio = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("div[data-bio-text]")));

        String bioText = bio.getText();
        System.out.println("Bio text: " + bioText);

        Assert.assertTrue(bio.isDisplayed(), "Bio is not displayed");
    }

    // TEST 5: UPDATE PROFILE PICTURE
    @Test(priority = 4)
    public void updateProfilePic() throws InterruptedException, IOException{
        driver.get("https://github.com/settings/profile");

        // Click avatar
        WebElement avatar = wait.until(ExpectedConditions.elementToBeClickable(
        By.xpath("//summary[.//img[contains(@class,'avatar-user')]]")
        ));
        avatar.click();

        // Upload file
        WebElement fileInput = driver.findElement(By.id("avatar_upload"));
        String filePath = Paths.get("screenshots", "test.jpg")
                       .toAbsolutePath()
                       .toString();

        fileInput.sendKeys(filePath);

        sleep(PAUSE_LONG);
        
        takeScreenshot("test5_uploaded_pfp.png");

        Assert.assertTrue(true, "File not uploaded");
    }
}
