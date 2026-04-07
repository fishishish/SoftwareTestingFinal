import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

import static java.lang.Thread.sleep;

public class CreateRepoTests {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final int PAUSE_MEDIUM = 1000;
    private static final int PAUSE_LONG = 2000;

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

    // CREATE PUBLIC REPO "Testrepo1"
    @Test(priority = 1)
    public void createPublicRepo() throws InterruptedException {

        driver.get("https://github.com/");
        sleep(PAUSE_MEDIUM);

        // Go to New Repository page
        driver.get("https://github.com/new");
        sleep(PAUSE_MEDIUM);

        // Enter repository name
        WebElement repoName = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("repository_name"))
        );
        repoName.sendKeys("Testrepo1");
        sleep(PAUSE_MEDIUM);

        // Click "Create repository"
        WebElement createBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button.first-in-line")
                )
        );
        createBtn.click();
        sleep(PAUSE_LONG);

        // Verify repo created
        Assert.assertTrue(
                driver.getCurrentUrl().contains("Testrepo1"),
                "Public repo was not created"
        );
    }

    // CREATE PRIVATE REPO "Testrepo2"
    @Test(priority = 2)
    public void createPrivateRepo() throws InterruptedException {

        driver.get("https://github.com/");
        sleep(PAUSE_MEDIUM);

        // Go to New Repository page
        driver.get("https://github.com/new");
        sleep(PAUSE_MEDIUM);

        // Enter repository name
        WebElement repoName = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("repository_name"))
        );
        repoName.sendKeys("Testrepo2");
        sleep(PAUSE_MEDIUM);

        // Select Private option
        WebElement privateOption = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.id("repository_visibility_private")
                )
        );
        privateOption.click();
        sleep(PAUSE_MEDIUM);

        // Click "Create repository"
        WebElement createBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button.first-in-line")
                )
        );
        createBtn.click();
        sleep(PAUSE_LONG);

        // Verify repo created
        Assert.assertTrue(
                driver.getCurrentUrl().contains("Testrepo2"),
                "Private repo was not created"
        );
    }
}
