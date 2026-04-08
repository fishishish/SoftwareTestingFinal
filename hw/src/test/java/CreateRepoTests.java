import java.time.Duration;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CreateRepoTests {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;
    private Actions actions;

    private static final String BASE_URL     = "https://github.com/";
    private static final String REPO_PUBLIC  = "TestRepository1";
    private static final String REPO_PRIVATE = "TestRepository2";

    private static final int PAUSE_SHORT  = 600;
    private static final int PAUSE_MEDIUM = 1200;
    private static final int PAUSE_LONG   = 2500;

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private void typeRepoName(String name) {
        WebElement field = null;

        try { field = driver.findElement(By.id("repository_name")); } catch (Exception ignored) {}

        if (field == null) {
            try {
                field = driver.findElement(
                        By.xpath("//label[contains(text(),'Repository name')]" +
                                "/following-sibling::input | " +
                                "//label[contains(text(),'Repository name')]/..//input[@type='text']")
                );
            } catch (Exception ignored) {}
        }

        if (field == null) {
            try {
                field = driver.findElement(
                        By.xpath("//h2[contains(text(),'General')]/following::input[@type='text'][1]")
                );
            } catch (Exception ignored) {}
        }

        Assert.assertNotNull(field, "Could not locate the Repository name input field");

        js.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", field);
        sleep(PAUSE_SHORT);

        actions.moveToElement(field).click().perform();
        sleep(PAUSE_SHORT);

        actions.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).perform();
        sleep(200);
        actions.sendKeys(Keys.DELETE).perform();
        sleep(200);

        actions.sendKeys(name).perform();
        sleep(PAUSE_SHORT);

        js.executeScript(
                "var nativeSetter = Object.getOwnPropertyDescriptor(" +
                        "  window.HTMLInputElement.prototype, 'value').set;" +
                        "nativeSetter.call(arguments[0], arguments[1]);" +
                        "arguments[0].dispatchEvent(new Event('input',  {bubbles:true}));" +
                        "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                field, name
        );
        sleep(PAUSE_SHORT);

        String actual = field.getAttribute("value");
        System.out.println(">>> Repo name field value after typing: [" + actual + "]");
        Assert.assertEquals(actual, name,
                "Field value mismatch — expected [" + name + "] but got [" + actual + "]");
    }

    private void setVisibility(String visibility) {
        WebElement dropdownBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath(
                                "//button[.//span[normalize-space()='Public'] or " +
                                        "         .//span[normalize-space()='Private'] or " +
                                        "         contains(@aria-label,'visibility') or " +
                                        "         contains(@aria-label,'Visibility')]"
                        )
                )
        );
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", dropdownBtn);
        sleep(300);
        js.executeScript("arguments[0].click();", dropdownBtn);
        sleep(PAUSE_SHORT);

        WebElement option = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath(
                                "//*[@role='menuitem' and contains(normalize-space(), '" + visibility + "')] | " +
                                        "//*[@role='option'   and contains(normalize-space(), '" + visibility + "')] | " +
                                        "//li[contains(normalize-space(), '" + visibility + "')]"
                        )
                )
        );
        js.executeScript("arguments[0].click();", option);
        sleep(PAUSE_SHORT);
    }

    private void createRepo(String repoName, String visibility) {
        driver.get(BASE_URL + "new");
        sleep(PAUSE_LONG);

        typeRepoName(repoName);
        setVisibility(visibility);

        WebElement createBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[.//span[text()='Create repository']]")
                )
        );
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", createBtn);
        sleep(300);
        js.executeScript("arguments[0].click();", createBtn);
        sleep(PAUSE_LONG);
    }

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-data-dir=C:\\GitHubProfile");
        options.addArguments("profile-directory=Profile1");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-first-run");
        options.addArguments("--no-default-browser-check");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-debugging-port=9222");

        driver = new ChromeDriver(options);
        js      = (JavascriptExecutor) driver;
        actions = new Actions(driver);

        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get(BASE_URL);
        sleep(PAUSE_MEDIUM);

        boolean isLoggedIn = !driver.findElements(
                By.cssSelector("meta[name='user-login']")).isEmpty();

        if (!isLoggedIn) {
            driver.quit();
            throw new RuntimeException(
                    "Not logged into GitHub. Log in manually to Profile1 first.");
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 1 — Navigate to new repo page
    // ─────────────────────────────────────────────────────────────
    @Test(priority = 1)
    public void testNavToCreateRepo() {
        driver.get(BASE_URL + "new");
        sleep(PAUSE_MEDIUM);

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/new"),
                "Did not navigate to the new repo page — URL: " + driver.getCurrentUrl()
        );
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 2 — Create a PUBLIC repository
    // ─────────────────────────────────────────────────────────────
    @Test(priority = 2)
    public void testCreatePublicRepo() {
        createRepo(REPO_PUBLIC, "Public");

        wait.until(ExpectedConditions.urlContains(REPO_PUBLIC));

        Assert.assertTrue(
                driver.getCurrentUrl().contains(REPO_PUBLIC),
                "Public repo not created — URL: " + driver.getCurrentUrl()
        );
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 3 — Create a PRIVATE repository
    // ─────────────────────────────────────────────────────────────
    @Test(priority = 3)
    public void testCreatePrivateRepo() {
        createRepo(REPO_PRIVATE, "Private");

        wait.until(ExpectedConditions.urlContains(REPO_PRIVATE));

        Assert.assertTrue(
                driver.getCurrentUrl().contains(REPO_PRIVATE),
                "Private repo not created — URL: " + driver.getCurrentUrl()
        );
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 4 — Negative: empty name should block submission
    // ─────────────────────────────────────────────────────────────
    @Test(priority = 4)
    public void testInvalidCreateRepo() {
        driver.get(BASE_URL + "new");
        sleep(PAUSE_MEDIUM);

        WebElement createRepoBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[.//span[text()='Create repository']]")
                )
        );
        createRepoBtn.click();
        sleep(PAUSE_MEDIUM);

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/new"),
                "Expected to stay on /new but navigated to: " + driver.getCurrentUrl()
        );
    }

    // ─────────────────────────────────────────────────────────────
    // TEST 5 — Negative: duplicate repo name should show error
    // ─────────────────────────────────────────────────────────────
    @Test(priority = 5)
    public void testDuplicateRepoName() {
        driver.get(BASE_URL + "new");
        sleep(PAUSE_LONG);

        typeRepoName(REPO_PUBLIC);
        sleep(PAUSE_MEDIUM);

        // GitHub shows "TestRepository1 already exists in this account" below the field
        WebElement errorMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(), 'already exists in this account')]")
                )
        );

        Assert.assertTrue(
                errorMsg.isDisplayed(),
                "Expected duplicate name error but none appeared"
        );
        System.out.println(">>> Duplicate error message shown: " + errorMsg.getText());
    }
    // ─────────────────────────────────────────────────────────────
    // TEST 6 — Verify both repos appear on the profile page
    // ─────────────────────────────────────────────────────────────
    @Test(priority = 6)
    public void testVerifyReposOnProfilePage() {
        WebElement userMeta = driver.findElement(By.cssSelector("meta[name='user-login']"));
        String username = userMeta.getAttribute("content");
        Assert.assertNotNull(username, "Could not read username from meta tag");
        System.out.println(">>> Checking profile for user: " + username);

        driver.get(BASE_URL + username + "?tab=repositories");
        sleep(PAUSE_LONG);

        WebElement publicRepo = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//a[@itemprop='name codeRepository' and contains(normalize-space(), '" + REPO_PUBLIC + "')] | " +
                                        "//h3[contains(normalize-space(), '" + REPO_PUBLIC + "')]//a | " +
                                        "//a[normalize-space()='" + REPO_PUBLIC + "']"
                        )
                )
        );
        Assert.assertTrue(
                publicRepo.isDisplayed(),
                REPO_PUBLIC + " was not found on the profile repositories page"
        );
        System.out.println(">>> Found public repo: " + publicRepo.getText());

        WebElement privateRepo = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(
                                "//a[@itemprop='name codeRepository' and contains(normalize-space(), '" + REPO_PRIVATE + "')] | " +
                                        "//h3[contains(normalize-space(), '" + REPO_PRIVATE + "')]//a | " +
                                        "//a[normalize-space()='" + REPO_PRIVATE + "']"
                        )
                )
        );
        Assert.assertTrue(
                privateRepo.isDisplayed(),
                REPO_PRIVATE + " was not found on the profile repositories page"
        );
        System.out.println(">>> Found private repo: " + privateRepo.getText());
    }
}
