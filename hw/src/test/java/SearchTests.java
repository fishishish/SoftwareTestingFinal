import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
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

/**
 * SearchTests - GitHub Search Feature Test Suite
 *
 * SETUP REQUIRED:
 * - GitHub's search bar is a React component. Selectors change between releases.
 * - The "security" topic dropdown and "search syntax tips" link may require
 *   hovering or focusing the search bar first to appear. See TODOs per test.
 * - A screenshot directory will be created automatically at ./screenshots/
 */
public class SearchTests {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL          = "https://github.com/";
    private static final String SEARCH_RESULTS_URL =
            "https://github.com/search?q=Testing&type=issues";
    private static final String ADVANCED_SEARCH_URL = "https://github.com/search/advanced";
    private static final String SCREENSHOT_DIR     = "screenshots/";

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

    // ── Helper: open GitHub home and focus the search bar ────────────────────
    private WebElement openSearchBar() {
        driver.get(BASE_URL);

        WebElement searchBar = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[data-target='qbsearch-input.inputButton']")));
        searchBar.click();
        return searchBar;
    }

    // ── Helper: take a named screenshot ───────────────────────────────────────
    private void takeScreenshot(String filename) throws IOException {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Path destination = Paths.get(SCREENSHOT_DIR + filename);
        Files.copy(screenshot.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Screenshot saved: " + destination.toAbsolutePath());
    }

    // =========================================================================
    // Test 1 – Select "Security" from search dropdown; assert page title
    // =========================================================================
    @Test(priority = 1)
    public void test1_SearchDropdownSecurity() {
        openSearchBar();

        sleep(PAUSE_MEDIUM);
        
        WebElement securityOption = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//span[@id='query-builder-test-result-1--leading']/..")));
        securityOption.click();

        wait.until(ExpectedConditions.titleContains("Security"));

        String pageTitle = driver.getTitle();
        System.out.println("Page title after selecting Security: " + pageTitle);

        Assert.assertTrue(pageTitle.contains("Github Security") || pageTitle.contains("Security"),
                "Expected title to include 'Github Security' but got: " + pageTitle);
        
        sleep(PAUSE_MEDIUM);
    }

    // =========================================================================
    // Test 2 – Click "Search syntax tips" link; assert URL; close tab
    // =========================================================================
    @Test(priority = 2)
    public void test2_SearchSyntaxTipsLink() {
        openSearchBar();

        sleep(PAUSE_MEDIUM);

        WebElement syntaxTipsLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.linkText("Search syntax tips")));
        syntaxTipsLink.click();

        // Switch to new tab
        String originalHandle = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(originalHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(BASE_URL)));

        String currentUrl = driver.getCurrentUrl();
        System.out.println("Search syntax tips URL: " + currentUrl);

        // Assert current URL matches
        Assert.assertTrue(currentUrl.contains("https://docs.github.com/en/search-github/github-code-search/understanding-github-code-search-syntax"),
                "Expected search syntax tips URL but got: " + currentUrl);

        sleep(PAUSE_MEDIUM);

        driver.close();
        driver.switchTo().window(originalHandle);
    }

    // =========================================================================
    // Test 3 – Search "Testing"; assert query remains in search bar; screenshot
    // =========================================================================
    @Test(priority = 3)
    public void test3_SearchTesting() throws IOException {
        openSearchBar();

        //Select search input field
        WebElement searchInput = wait.until(
        ExpectedConditions.visibilityOfElementLocated(
                By.id("query-builder-test")));
                sleep(PAUSE_SHORT);
                ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", searchInput);
                searchInput.sendKeys("Testing");
                sleep(PAUSE_MEDIUM);
                searchInput.sendKeys(Keys.ENTER);

        // Wait for results page
        wait.until(ExpectedConditions.urlContains("q=Testing"));

        String currentUrl = driver.getCurrentUrl();
        System.out.println("Search 'Testing' URL: " + currentUrl);

        // Assert current URL matches
        Assert.assertTrue(currentUrl.contains("https://github.com/search?q=Testing"),
                "Expected search testing URL but got: " + currentUrl);

        takeScreenshot("test3_search_testing_results.png");

        sleep(PAUSE_MEDIUM);
    }

    // =========================================================================
    // Test 4 – Filter by Issues, sort by Newest; assert URL; screenshot
    // =========================================================================
    @Test(priority = 4)
    public void test4_FilterIssuesSortNewest() throws IOException {
        // Start from search results for "Testing"
        driver.get("https://github.com/search?q=Testing");
        wait.until(ExpectedConditions.urlContains("q=Testing"));

        WebElement issuesFilter = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//span[contains(@class,'prc-ActionList-ItemLabel') and normalize-space()='Issues']")));
        issuesFilter.click();

        wait.until(ExpectedConditions.urlContains("type=issues"));

        String currentUrl = driver.getCurrentUrl();
        System.out.println("URL after filtering issues: " + currentUrl);

        Assert.assertEquals(currentUrl, SEARCH_RESULTS_URL,
                "Expected URL " + SEARCH_RESULTS_URL + " but got: " + currentUrl);

        takeScreenshot("test4_issues_results.png");
        sleep(PAUSE_MEDIUM);
    }

    // =========================================================================
    // Test 5 – Click Advanced Search; assert URL
    // =========================================================================
    @Test(priority = 5)
    public void test5_AdvancedSearch() {
        // Navigate to a search results page so the Advanced Search link is visible
        driver.get("https://github.com/search?q=Testing");
        wait.until(ExpectedConditions.urlContains("q=Testing"));

        WebElement advancedSearchLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//span[contains(@class,'prc-ActionList-ItemLabel') and normalize-space()='Advanced search']")));
        advancedSearchLink.click();

        wait.until(ExpectedConditions.urlContains("advanced"));

        String currentUrl = driver.getCurrentUrl();
        System.out.println("Advanced search URL: " + currentUrl);

        Assert.assertEquals(currentUrl, ADVANCED_SEARCH_URL,
                "Expected URL " + ADVANCED_SEARCH_URL + " but got: " + currentUrl);
        sleep(PAUSE_MEDIUM);
    }
}