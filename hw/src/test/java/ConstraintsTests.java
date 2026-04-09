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

public class ConstraintsTests {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL = "https://github.com/";
    private static final String ACCOUNT_URL = "https://github.com/settings/admin";
    private static final String APPEARANCES_URL = "https://github.com/settings/appearance";
    private static final String LOGOUT_URL = "https://github.com/logout";
    private static final String LOGIN_URL = "https://github.com/login";

    private static final String VALID_USERNAME = "TestFinal777";
    private static final String VALID_PASSWORD = "ValidExample8!";


    private static final int PAUSE_SHORT = 500;   // brief beat between actions
    private static final int PAUSE_MEDIUM = 1000;   // let the viewer read a result
    private static final int PAUSE_LONG = 2000;

    @BeforeClass
    public void setUp() throws InterruptedException {
        ChromeOptions options = new ChromeOptions();

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


    // ****************** Test 1 - Popup Handling Constraint ******************
    // verify automation can handle popups - continue interacting after confirmation.
    @Test (priority = 1)
    public void testPopupAppearsAndClosesProperly() throws InterruptedException{

        // open account settings page
        driver.get(ACCOUNT_URL);
        sleep(PAUSE_SHORT);

        // open username change popup
        WebElement changeUsernameBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("dialog-show-rename-warning-dialog"))
        );
        changeUsernameBtn.click();
        sleep(PAUSE_MEDIUM);

        // verify popup appears
        WebElement confirmBtn = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("button[data-show-dialog-id='rename-form-dialog']")
                )
        );
        sleep(PAUSE_MEDIUM);

        Assert.assertTrue(confirmBtn.isDisplayed(), "Confirmation popup did not appear");

        // close popup by clicking confirm
        confirmBtn.click();
        sleep(PAUSE_MEDIUM);

        // wait until input is clickable
        WebElement usernameInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("login"))
        );

        // click first
        usernameInput.click();
        sleep(PAUSE_MEDIUM);
        // clear input just in case
        usernameInput.clear();
        sleep(PAUSE_MEDIUM);
        // enter username
        usernameInput.sendKeys("test123");
        sleep(PAUSE_MEDIUM);

        Assert.assertTrue(
                usernameInput.getAttribute("value").length() > 0,
                "Unable to interact with input after popup"
        );

        // verify interaction worked
        Assert.assertTrue(
                usernameInput.getAttribute("value").length() > 0,
                "Unable to interact with input after popup"
        );
}

    // ****************** Test 2 - Dynamic Element / Wait Constraint ******************
    // ensure element is fully loaded before performing action - shows need for waits.
    @Test (priority = 2)
    public void testElementRequiresWait() throws InterruptedException{

        // go to appearances page
        driver.get(APPEARANCES_URL);
        sleep(PAUSE_MEDIUM);

        WebElement dropdown = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("color_mode_type_select"))
        );

        // open dropdown
        dropdown.click();
        sleep(PAUSE_MEDIUM);

        Assert.assertTrue(dropdown.isDisplayed(), "Dropdown not interactable after wait");
    }

    // ****************** Test 3 - Visibility / Scrolling Constraint ******************
    // elements may require scrolling into view before interaction.
    @Test (priority = 3)
    public void testElementNotClickableUntilScrolled() throws InterruptedException {

        // go to appearances page
        driver.get(APPEARANCES_URL);
        sleep(PAUSE_MEDIUM);

        WebElement toggle = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[aria-labelledby='increase-contrast-label']")
                )
        );

        // scroll
        JavascriptExecutor exe = (JavascriptExecutor) driver;

        exe.executeScript("window.scroll(0, 500)","");
        sleep(PAUSE_SHORT);

        // toggle ON
        toggle.click();
        sleep(PAUSE_MEDIUM);


        // verify ON
        Assert.assertEquals(toggle.getAttribute("aria-pressed"), "true");

        // toggle OFF
        toggle.click();
        sleep(PAUSE_MEDIUM);
    }

    // ****************** Test 4 - Navigation Interruption Constraint ******************
    // demonstrates that user actions like navigation can interrupt workflows,
    // automation has to handle page transitions without breaking.
    @Test(priority = 4)
    public void testNavigationInterruption() throws InterruptedException {

        // go to appearances page
        driver.get(APPEARANCES_URL);
        sleep(PAUSE_MEDIUM);

        WebElement dropdown = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("color_mode_type_select"))
        );
        dropdown.click();
        sleep(PAUSE_MEDIUM);

        // navigate away mid-action
        driver.get("https://github.com/settings/account");
        sleep(PAUSE_MEDIUM);

        // verify new page loaded successfully
        Assert.assertTrue(
                driver.getCurrentUrl().contains("account"),
                "Navigation interruption failed"
        );
    }
    // ****************** Test 5 - Authentication / Login Constraint ******************
    // verifies authentication introduces constraints such as potential security steps (2FA).
    @Test(priority = 5)
    public void test2FAConstraint() throws InterruptedException{

        // go to log out page
        driver.get(LOGOUT_URL);
        sleep(PAUSE_MEDIUM);

        // sign out of account
        WebElement signOutBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("input[value='Sign out']")
                )
        );

        signOutBtn.click();
        sleep(PAUSE_MEDIUM);

        // go to login page
        driver.get(LOGIN_URL);
        sleep(PAUSE_MEDIUM);

        WebElement username = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("login_field"))
        );

        // enter a valid username
        username.sendKeys(VALID_USERNAME);
        sleep(PAUSE_MEDIUM);
        // enter a valid password
        driver.findElement(By.id("password")).sendKeys(VALID_PASSWORD);
        sleep(PAUSE_MEDIUM);
        // submit with valid credentials
        driver.findElement(By.name("commit")).click();
        sleep(PAUSE_LONG);

        // verify login flow (2FA)
        Assert.assertTrue(
                driver.getCurrentUrl().contains("github"),
                "Login flow did not proceed as expected"
        );
    }
}