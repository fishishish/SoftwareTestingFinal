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

  
    private static final String SEARCH_KEYWORD   = "selenium";
    private static final int    REPO_RESULT_INDEX = 0; // 0 = first result, 1 = second, etc.
    private static final String YOUR_USERNAME     = "STestacc";

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

    // ✅ CHANGE 2: Reusable helper — search, pick Nth result, fork it
    
    private void selectAndForkRepo(String keyword, int resultIndex) throws InterruptedException {

        // ── Step 1: Search ──────────────────────────────────────────────────
        driver.get("https://github.com/search?q=" + keyword + "&type=repositories");
        sleep(PAUSE_MEDIUM);

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("ul[data-testid='results-list']")
        ));
        sleep(PAUSE_MEDIUM);

        // ── Step 2: Collect result links and pick the Nth one ───────────────
        // Each result card contains an <a> whose href is the repo path
        List<WebElement> repoLinks = driver.findElements(
                By.cssSelector("ul[data-testid='results-list'] li a[href*='/'][data-testid='link-to-search-result']")
        );

        Assert.assertTrue(
                repoLinks.size() > resultIndex,
                "Not enough results — found " + repoLinks.size() + ", needed index " + resultIndex
        );

        // Read the href so we don't lose the element after navigation
        String repoUrl = repoLinks.get(resultIndex).getAttribute("href");
        System.out.println("Forking repo: " + repoUrl);

        // ── Step 3: Open the repo page ──────────────────────────────────────
        driver.get(repoUrl);
        sleep(PAUSE_MEDIUM);

        // ── Step 4: Navigate directly to the fork page ──────────────────────
        driver.get(repoUrl + "/fork");
        sleep(PAUSE_LONG);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("form")));
        sleep(PAUSE_MEDIUM);

        // ── Step 5: Scroll to and JS-click "Create fork" ────────────────────
        WebElement createForkBtn = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//button[@type='submit'][contains(.,'Create fork')]")
                )
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", createForkBtn);
        sleep(PAUSE_MEDIUM);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", createForkBtn);
        sleep(PAUSE_LONG);

        // ── Step 6: Verify redirect lands on your account ───────────────────
        wait.until(ExpectedConditions.urlContains(YOUR_USERNAME));
        Assert.assertTrue(
                driver.getCurrentUrl().contains(YOUR_USERNAME),
                "Fork did not redirect to your account. Current URL: " + driver.getCurrentUrl()
        );
    }

    // 3️⃣ TEST: SEARCH → SELECT Nth REPO → FORK IT
    @Test(priority = 3)
    public void forkFirstRepoFromContributions() throws InterruptedException {

        // ── Step 1: Go to the contributions tab on your profile ──────────────
        // The "contributions" view lives at ?tab=overview on the profile page
        driver.get("https://github.com/" + YOUR_USERNAME + "?tab=overview");
        sleep(PAUSE_LONG);

        // ── Step 2: Find repos listed in the contribution activity feed ──────

        String repoUrl = null;

        // ── Attempt A: "Contributed to" sidebar block ────────────────────────
        try {
            // The sidebar heading says "X repositories" under "Contributed to"
            // and each repo is a plain <a> link inside that section.
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
                System.out.println("[Attempt A] Found contributed-to repo in sidebar: " + repoUrl);
            }
        } catch (Exception e) {
            System.out.println("[Attempt A] Sidebar block not found, trying activity feed...");
        }

        // ── Attempt B: Activity feed repo links ──────────────────────────────
        if (repoUrl == null) {
            List<WebElement> activityRepoLinks = driver.findElements(
                    By.xpath(
                            "//div[@id='js-contribution-activity']" +
                                    "//a[@data-hovercard-type='repository']"
                    )
            );

            if (!activityRepoLinks.isEmpty()) {
                repoUrl = activityRepoLinks.get(0).getAttribute("href");
                System.out.println("[Attempt B] Found repo in activity feed: " + repoUrl);
            }
        }

        // ── Attempt C: Any repository hovercard link on the overview page ────
        // Broadest fallback — grabs ANY repo link with a hovercard attribute
        if (repoUrl == null) {
            List<WebElement> hovercardLinks = driver.findElements(
                    By.xpath("//a[@data-hovercard-type='repository']")
            );

            for (WebElement link : hovercardLinks) {
                String href = link.getAttribute("href");
                // Skip links pointing to the user's own repos
                if (href != null && !href.contains("/" + YOUR_USERNAME + "/")) {
                    repoUrl = href;
                    System.out.println("[Attempt C] Found repo via hovercard: " + repoUrl);
                    break;
                }
            }
        }

        Assert.assertNotNull(repoUrl,
                "Could not find any contributed-to repo on the overview page. " +
                        "Make sure the account has public contribution activity.");

        // ── Step 3: Open the repo to confirm it exists and is accessible ─────
        driver.get(repoUrl);
        sleep(PAUSE_MEDIUM);

        // Confirm we landed on a valid repo page (has the repo header)
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("main#js-repo-pjax-container, div[data-pjax-container]," +
                        " h1.d-flex, [itemprop='name']")
        ));

        // ── Step 4: Navigate to the fork page ───────────────────────────────
        String forkUrl = repoUrl.replaceAll("/$", "") + "/fork";
        System.out.println("Navigating to fork page: " + forkUrl);
        driver.get(forkUrl);
        sleep(PAUSE_LONG);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("form")));
        sleep(PAUSE_MEDIUM);

        // ── Step 5: Click "Create fork" via JavaScript ───────────────────────
        WebElement createForkBtn = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//button[@type='submit'][contains(.,'Create fork')]")
                )
        );
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);", createForkBtn);
        sleep(PAUSE_MEDIUM);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", createForkBtn);
        sleep(PAUSE_LONG);

        // ── Step 6: Assert the fork redirected to your account ───────────────
        wait.until(ExpectedConditions.urlContains(YOUR_USERNAME));
        Assert.assertTrue(
                driver.getCurrentUrl().contains(YOUR_USERNAME),
                "Fork did not redirect to your account. Current URL: " + driver.getCurrentUrl()
        );

        System.out.println("Fork successful! Landed at: " + driver.getCurrentUrl());
    }
}

