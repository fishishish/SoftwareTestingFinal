import java.time.Duration;

import org.openqa.selenium.By;
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



public class CreateRepoTests {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL = "https://github.com/";
    private static final String NEW_REPO_URL = "https://github.com/new";
    private static final String REPO_TITLE = "Test Repository";


    private static final int PAUSE_MEDIUM = 1000;   // let the viewer read a result
    private static final int PAUSE_LONG   = 2000;

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
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

        driver.get(BASE_URL);

        sleep(PAUSE_MEDIUM);
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }


// ****************** Test 1 - Navigate to Create Repository ******************
    @Test (priority = 1)
    public void testNavToCreateRepo () throws InterruptedException {

        WebElement addBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("_R_5jpb_")));
        addBtn.click();

        sleep(PAUSE_LONG);

         WebElement addRepoBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("New repository")));
        addRepoBtn.click();

        Assert.assertTrue(
                driver.getCurrentUrl().contains("https://github.com/new"),
                "Expected URL but got" + driver.getCurrentUrl());
    }

// ****************** Test 2 - Test Public Repo Creation ******************
    @Test (priority = 2)
    public void testCreatePublicRepo () throws InterruptedException {
        driver.get(NEW_REPO_URL);

        WebElement nameRepoInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("repository-name-input")));
        nameRepoInput.click();
        nameRepoInput.sendKeys(REPO_TITLE);

        sleep(PAUSE_MEDIUM);            // Pause to show title input

        WebElement createRepoBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[.//span[text()='Create repository']]")));
        createRepoBtn.click();

        sleep(PAUSE_LONG); 
 
        // Assert current URL matches expected URL
        String testRepoURL = "https://github.com/TestFinal777/Test-Repository";
        String currentUrl = driver.getCurrentUrl();

        Assert.assertEquals(currentUrl, testRepoURL, "The current URL does not match the expected URL - Repo not created successfully");

    }

// ****************** Test 3 - Test Private Repo Creation ******************
    @Test (priority = 3)
    public void testCreatePrivateRepo () throws InterruptedException {
        driver.get(NEW_REPO_URL);

        WebElement nameRepoInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("repository-name-input")));
        nameRepoInput.click();
        nameRepoInput.sendKeys(REPO_TITLE);

        sleep(PAUSE_MEDIUM);            // Pause to show title input

        WebElement visibilityButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("visibility-anchor-button")));
                visibilityButton.click();
        sleep(PAUSE_MEDIUM);            // Pause to show dropdown

        // Wait for the Private option to be visible
        WebElement privateOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[@role='menuitemradio']//span[text()='Private']/ancestor::li")));
        privateOption.click();
        sleep(PAUSE_LONG);              // Pause to show Private was selected

        // Assert that dropdown button now shows Private
        String buttonText = visibilityButton.getText().trim();
        Assert.assertTrue(buttonText.contains("Private"), "Error - Visibility not changed to private");

    }

// ****************** Test 4 - Test Invalid Nameless Repo Creation ******************
    @Test (priority = 4)
    public void testInvalidCreateRepo () throws InterruptedException {
        driver.get(NEW_REPO_URL);

        WebElement createRepoBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[.//span[text()='Create repository']]")));
        createRepoBtn.click();

        // Find error message for not naming repo
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("RepoNameInput-message")));

        // Assert the error message text
        String expectedMessage = "Name cannot be blank";
        Assert.assertEquals(errorMessage.getText().trim(), expectedMessage, "Error message is not as expected");

        sleep(PAUSE_LONG);            // Pause to show error message

    }

// ****************** Test 5 - Test Duplicate Repo Error ******************
    @Test (priority = 5)
    public void testDuplicateCreateRepo () throws InterruptedException {
        driver.get(NEW_REPO_URL);

        WebElement nameRepoInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("repository-name-input")));
        nameRepoInput.click();
        nameRepoInput.sendKeys(REPO_TITLE);

        sleep(PAUSE_MEDIUM);            // Pause to show title input

        WebElement createRepoBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[.//span[text()='Create repository']]")));
        createRepoBtn.click();
 
        // Find error message for naming the repo an existing name
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("RepoNameInput-message")));

        // Assert the error message text
        String expectedMessage = "Test-Repository already exists in this account";
        Assert.assertEquals(errorMessage.getText().trim(), expectedMessage, "Error message is not as expected");

        sleep(PAUSE_LONG);            // Pause to show error message

    }

}