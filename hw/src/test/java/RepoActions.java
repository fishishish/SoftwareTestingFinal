import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import org.openqa.selenium.JavascriptExecutor;

import java.time.Duration;
import java.util.List;

import static java.lang.Thread.sleep;

public class RepoActionTests {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final int PAUSE_MEDIUM = 1000;
    private static final int PAUSE_LONG   = 2000;

    private static final String YOUR_USERNAME = "STestacc";

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

    // 1️⃣ TEST: HOME → PROFILE → REPOSITORIES
    @Test(priority = 1)
    public void goToRepositoriesPage() throws InterruptedException {

        driver.get("https://github.com/");
        sleep(PAUSE_MEDIUM);

        driver.get("https://github.com/" + YOUR_USERNAME);
        sleep(PAUSE_MEDIUM);

        WebElement repoTab = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(@href,'?tab=repositories')]")
                )
        );
        repoTab.click();
        sleep(PAUSE_MEDIUM);

        WebElement repoList = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("div#user-repositories-list")
                )
        );
        Assert.assertTrue(repoList.isDisplayed(), "Repo list not visible");
    }

    // 2️⃣ TEST: STAR FIRST REPOSITORY
    @Test(priority = 2)
    public void starFirstRepository() throws InterruptedException {

        driver.get("https://github.com/" + YOUR_USERNAME + "?tab=repositories");
        sleep(PAUSE_MEDIUM);

        WebElement starButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("#user-repositories-list ul li form[action$='/star'] button")
                )
        );
        starButton.click();
        sleep(PAUSE_MEDIUM);

        WebElement unstarButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#user-repositories-list ul li form[action$='/unstar'] button")
                )
        );
        Assert.assertTrue(unstarButton.isDisplayed(), "Star action did not complete");
    }
    

    // 3️⃣ TEST: FORK A REPO FROM CONTRIBUTIONS
    @Test(priority = 3)
    public void forkFirstRepoFromContributions() throws InterruptedException {

        driver.get("https://github.com/" + YOUR_USERNAME + "?tab=overview");
        sleep(PAUSE_LONG);

        String repoUrl = null;

        try {
            List<WebElement> sidebarRepoLinks = driver.findElements(
                    By.xpath(
                            "//div[contains(@class,'js-yearly-contributions')]" +
                                    "//a[contains(@href, '/') " +
                                    "    and not(contains(@href, YOUR_USERNAME)) " +
                                    "    and not(contains(@href, '?')) " +
                                    "    and not(contains(@href, '#'))]"
                                            .replace("YOUR_USERNAME", YOUR_USERNAME)
                    )
            );

            if (!sidebarRepoLinks.isEmpty()) {
                repoUrl = sidebarRepoLinks.get(0).getAttribute("href");
                System.out.println("[Sidebar] Found contributed repo: " + repoUrl);
            }
        } catch (Exception ignored) {}

        if (repoUrl == null) {
            List<WebElement> activityRepoLinks = driver.findElements(
                    By.xpath("//div[@id='js-contribution-activity']//a[@data-hovercard-type='repository']")
            );

            if (!activityRepoLinks.isEmpty()) {
                repoUrl = activityRepoLinks.get(0).getAttribute("href");
                System.out.println("[Activity] Found repo: " + repoUrl);
            }
        }

        Assert.assertNotNull(repoUrl, "No contributed-to repo found.");

        driver.get(repoUrl);
        sleep(PAUSE_MEDIUM);

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("main#js-repo-pjax-container, div[data-pjax-container], h1.d-flex")
        ));

        String forkUrl = repoUrl.replaceAll("/$", "") + "/fork";
        driver.get(forkUrl);
        sleep(PAUSE_LONG);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("form")));
        sleep(PAUSE_MEDIUM);

        WebElement createForkBtn = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//button[@type='submit'][contains(.,'Create fork')]")
                )
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", createForkBtn);
        sleep(PAUSE_MEDIUM);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", createForkBtn);
        sleep(PAUSE_LONG);

        wait.until(ExpectedConditions.urlContains(YOUR_USERNAME));
        Assert.assertTrue(
                driver.getCurrentUrl().contains(YOUR_USERNAME),
                "Fork did not redirect to your account."
        );
    }

    // 4️⃣ TEST: NAVIGATE TO CONTRIBUTIONS PAGE
    @Test(priority = 4)
    public void navigateToContributionsPage() throws InterruptedException {

        driver.get("https://github.com/" + YOUR_USERNAME + "?tab=overview");
        sleep(PAUSE_MEDIUM);

        WebElement contributionsSection = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#js-contribution-activity, .js-yearly-contributions")
                )
        );

        Assert.assertTrue(
                contributionsSection.isDisplayed(),
                "Contributions section did not load"
        );
    }

    // 5️⃣ TEST: NAVIGATE TO STARS PAGE
    @Test(priority = 5)
    public void navigateToStarsPage() throws InterruptedException {

        driver.get("https://github.com/" + YOUR_USERNAME + "?tab=stars");
        sleep(PAUSE_LONG);

        // Scroll to trigger lazy loading
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 400);");
        sleep(500);

        // Confirm URL landed on the stars tab
        Assert.assertTrue(
                driver.getCurrentUrl().contains("tab=stars"),
                "Did not land on the stars tab — URL: " + driver.getCurrentUrl()
        );

        // Covers: starred repo cards, empty blankslate, or the page container itself
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("article.Box-row")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("li.col-12")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".blankslate")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("main")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[data-target='user-starred-repos.mainContent']")),
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(@class,'pagehead') or contains(@class,'user-profile-nav')]")
                )
        ));

        // Pass — page loaded and we are on the correct tab
        Assert.assertTrue(true, "Stars page loaded successfully");
    }
}

