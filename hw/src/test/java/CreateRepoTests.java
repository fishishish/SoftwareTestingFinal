import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;

public class CreateRepoTests {

    private WebDriver driver;
    private WebDriverWait wait;

    private void pause(long ms) {
        try { Thread.sleep(ms); } catch (Exception ignored) {}
    }

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-data-dir=C:\\GitHubProfile");
        options.addArguments("profile-directory=Profile1");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void createPublicAndPrivateRepos() {

        // ---------- CREATE PUBLIC REPO ----------
        driver.get("https://github.com/new");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("repository_name")))
                .sendKeys("Testrepo1");

        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        pause(1000);

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.first-in-line"))).click();
        pause(2000);

        // ---------- CREATE PRIVATE REPO ----------
        driver.get("https://github.com/new");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("repository_name")))
                .sendKeys("Testrepo2");

        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        pause(1000);

        wait.until(ExpectedConditions.elementToBeClickable(By.id("repository_visibility_private"))).click();
        pause(1000);

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.first-in-line"))).click();
        pause(2000);
    }
