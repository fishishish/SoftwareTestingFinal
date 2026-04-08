import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import static java.lang.Thread.sleep;

public class ProfileTests {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String EMAIL_TO_TEST  = "Testusername@gmail.com";
    private static final String GITHUB_USERNAME = "STestacc";
    private static final int    PAUSE_MEDIUM    = 1000;
    private static final int    PAUSE_LONG      = 2000;

    @BeforeClass
    public void setUp() throws InterruptedException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-data-dir=C:\\GitHubProfile");
        options.addArguments("profile-directory=Profile1");
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

    // 1️⃣ TEST: HOME PAGE → DIRECT PROFILE PAGE
    @Test(priority = 1)
    public void openProfileDirectFromHome() throws InterruptedException {
        driver.get("https://github.com/");
        sleep(PAUSE_MEDIUM);

        driver.get("https://github.com/" + GITHUB_USERNAME);
        sleep(PAUSE_MEDIUM);

        Assert.assertTrue(
                driver.getCurrentUrl().contains(GITHUB_USERNAME),
                "Did not navigate to profile page"
        );
    }

    // 2️⃣ TEST: NAVIGATE TO EMAILS SETTINGS PAGE
    @Test(priority = 2)
    public void navigateToEmail() throws InterruptedException {
        driver.get("https://github.com/");
        sleep(PAUSE_MEDIUM);

        driver.get("https://github.com/settings/profile");
        sleep(PAUSE_MEDIUM);

        WebElement emailsTab = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a[href='/settings/emails']")
                )
        );
        emailsTab.click();
        sleep(PAUSE_MEDIUM);

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/settings/emails"),
                "Did not navigate to emails settings page"
        );
    }

    // 3️⃣ TEST: ADD EMAIL ADDRESS
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

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(), '" + EMAIL_TO_TEST + "')]")
                ),
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".flash-success, .Toast--success, [data-testid='success-banner']")
                )
        ));

        Assert.assertTrue(true, "Add email flow completed without error");
    }

    // 4️⃣ TEST: VERIFY PROFILE BIO VISIBLE
    @Test(priority = 4)
    public void verifyProfileBioVisible() {
        driver.get("https://github.com/" + GITHUB_USERNAME);

        WebElement profileSection = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(
                                "[data-testid='user-profile-bio'], .p-name, " +
                                        ".js-user-profile-bio, h1.vcard-names"
                        )
                )
        );

        Assert.assertTrue(
                profileSection.isDisplayed(),
                "Profile name/bio section should be visible on the profile page"
        );
    }
}
