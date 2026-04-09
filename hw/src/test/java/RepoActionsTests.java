import static java.lang.Thread.sleep;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

public class RepoActionsTests {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL = "https://github.com/";
    private static final int PAUSE_MEDIUM = 1000;
    private static final int PAUSE_LONG   = 2000;

    private static final String YOUR_USERNAME = "TestFinal777";

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

    // TEST 1: NAVIGATE TO REPO LIST
    @Test(priority = 1)
    public void goToRepositoriesPage() throws InterruptedException {
        driver.get("https://github.com/" + YOUR_USERNAME);
        sleep(PAUSE_MEDIUM);

        WebElement repoTab = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(@href,'?tab=repositories')]")
                )
        );
        repoTab.click();
        sleep(PAUSE_LONG); // Pause to show repo list

        WebElement repoList = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("div#user-repositories-list")
                )
        );
        Assert.assertTrue(repoList.isDisplayed(), "Repo list not visible");
    }

    // TEST 2: STAR FIRST REPOSITORY
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

    // TEST 3: SCROLL TO BOTTOM OF CONTRIBUTIONS PAGE, SLOW SCROLL TO SHOW ALL CONTRIBUTIONS
    @Test(priority = 3)
    public void navigateToContributionsPage() throws InterruptedException {
        driver.get("https://github.com/" + YOUR_USERNAME);
        sleep(PAUSE_MEDIUM);            // Pause to show contributions page

        // Wait for contributions section to load first
        WebElement contributionsSection = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#js-contribution-activity, .js-yearly-contributions")
                )
        );

        // Slow scroll to bottom of the page
        JavascriptExecutor js = (JavascriptExecutor) driver;
        long scrollHeight = (long) js.executeScript("return document.body.scrollHeight;");

        for (int i = 0; i < scrollHeight; i += 100) {
                js.executeScript("window.scrollTo(0, arguments[0]);", i);
                Thread.sleep(100); // pause 100ms between scrolls
        }

        Assert.assertTrue(
                contributionsSection.isDisplayed(),
                "Contributions section did not load"
        );
    }

    // TEST 4: NAVIGATE TO STARS PAGE
    @Test(priority = 4)
    public void navigateToStarsPage() throws InterruptedException {
        driver.get(BASE_URL);

        WebElement avatarBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//img[@data-testid='github-avatar']/ancestor::button")));
        avatarBtn.click();
        sleep(PAUSE_LONG);

        WebElement profileBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='Stars']")));
        profileBtn.click();
        sleep(PAUSE_LONG);

        // Assert current URL matches expected URL
        Assert.assertTrue(
                driver.getCurrentUrl().contains("tab=stars"),
                "Did not land on the stars tab — URL: " + driver.getCurrentUrl()
        );
    }

    // TEST 5: DELETE A REPOSITORY
    @Test(priority = 5)
    public void deleteARepository() throws InterruptedException {
        driver.get("https://github.com/TestFinal777/Test-Repository/settings");

        // Click "Delete this repository" button to open the dialog
        WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("dialog-show-repo-delete-menu-dialog")));
        deleteButton.click();
        sleep(PAUSE_LONG);

        // Click throught the repo deletion menu
        WebElement stage1Button = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("repo-delete-proceed-button")));
        stage1Button.click();
        sleep(PAUSE_LONG);
        WebElement stage2Button = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("repo-delete-proceed-button")));
        stage2Button.click();
        sleep(PAUSE_LONG);

        // Enter repository name
        WebElement inputBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("verification_field")));
        inputBox.click();
        inputBox.sendKeys("TestFinal777/Test-Repository");
        sleep(PAUSE_LONG);

        // Click button to delete repository
        WebElement finalDeleteButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.js-repo-delete-proceed-button.Button--danger")));
        finalDeleteButton.click();

        WebElement flashMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.cssSelector("div.js-flash-alert")));

        // Assert repository was successfully deleted
        String expectedText = "Your repository \"TestFinal777/Test-Repository\" was successfully deleted.";
        Assert.assertEquals(flashMessage.getText(), expectedText, 
                "Error - Expected message: 'Your repository \"TestFinal777/Test-Repository\" was successfully deleted.' Actual message:" + flashMessage.getText() );
        }

}


