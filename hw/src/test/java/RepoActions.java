import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

import static java.lang.Thread.sleep;

public class RepoActionTests {

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

    // Check REPOSITORIES
    @Test(priority = 1)
    public void goToRepositoriesPage() throws InterruptedException {

        driver.get("https://github.com/");
        sleep(PAUSE_MEDIUM);

        // Go to your profile page
        driver.get("https://github.com/STestacc");
        sleep(PAUSE_MEDIUM);

        // Click the "Repositories" tab
        WebElement repoTab = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(@href,'?tab=repositories')]")
                )
        );
        repoTab.click();
        sleep(PAUSE_MEDIUM);

        // Verify repo list is visible
        WebElement repoList = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("div#user-repositories-list")
                )
        );

        Assert.assertTrue(repoList.isDisplayed(), "Repo list not visible");
    }

    // STAR FIRST REPOSITORY (from repo list)
    @Test(priority = 2)
    public void starFirstRepository() throws InterruptedException {

        // Go directly to your repositories page
        driver.get("https://github.com/STestacc?tab=repositories");
        sleep(PAUSE_MEDIUM);

        // ⭐ Star button directly in the repo list
        WebElement starButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("#user-repositories-list ul li form[action$='/star'] button")
                )
        );
        starButton.click();
        sleep(PAUSE_MEDIUM);

        // Verify it changed to Unstar
        WebElement unstarButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#user-repositories-list ul li form[action$='/unstar'] button")
                )
        );

        Assert.assertTrue(unstarButton.isDisplayed(), "Star action did not complete");
    }

    // SEARCH → OPEN FIRST RESULT → FORK → CREATE FORK
    @Test(priority = 3)
    public void searchAndForkRepository() throws InterruptedException {

        // 1️⃣ Search GitHub for selenium repos
        driver.get("https://github.com/search?q=selenium&type=repositories");
        sleep(PAUSE_MEDIUM);

        // 2️⃣ Click first repo in search results
        WebElement firstResult = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("ul[data-testid='results-list'] li div a.v-align-middle")
                )
        );
        firstResult.click();
        sleep(PAUSE_MEDIUM);

        // 3️⃣ Click Fork button
        WebElement forkButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[aria-label='Fork your own copy of this repository']")
                )
        );
        forkButton.click();
        sleep(PAUSE_MEDIUM);

        // 4️⃣ Click "Create fork" in modal
        WebElement createForkBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Create fork')]")
                )
        );
        createForkBtn.click();
        sleep(PAUSE_LONG);

        // 5️⃣ Verify redirect to your fork
        Assert.assertTrue(
                driver.getCurrentUrl().contains("STestacc"),
                "Fork did not redirect to your account"
        );
    }
}

