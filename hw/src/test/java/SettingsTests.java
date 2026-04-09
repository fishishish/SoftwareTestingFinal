import static java.lang.Thread.sleep;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SettingsTests {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL = "https://github.com/";
    private static final String SETTINGS_URL = "https://github.com/settings";


    private static final int PAUSE_SHORT  = 500;   // brief beat between actions
    private static final int PAUSE_MEDIUM = 1000;   // let the viewer read a result
    private static final int PAUSE_LONG   = 2000;

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


// ****************** Test 1 - Navigate to Settings ******************
    @Test (priority = 1)
    public void testNavigateToSettings () throws InterruptedException {

        // setup chrome profile, uncomment long sleep to open chrome driver in set folder
        // gives enough time to login on first test, this can then be commented out
        // sleep(10000);

        // open settings page
        driver.get(SETTINGS_URL);

        wait.until(ExpectedConditions.urlContains("settings"));

        Assert.assertTrue(driver.getCurrentUrl().contains("settings"),
                "Failed to navigate to settings page.");

        sleep(PAUSE_LONG);


    }

    // ****************** Test 2 - Update User Name ******************
    @Test (priority = 2)
    public void testUpdateUserName() throws InterruptedException {

        // click on account link
        WebElement accountLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Account"))
        );

        accountLink.click();

        sleep(PAUSE_MEDIUM);

        // click change username
        WebElement changeUsernameBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.id("dialog-show-rename-warning-dialog")
                )
        );

        changeUsernameBtn.click();

        sleep(PAUSE_MEDIUM);

        // click confirmation
        WebElement confirmBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[data-show-dialog-id='rename-form-dialog']")
                )
        );
        confirmBtn.click();

        sleep(PAUSE_MEDIUM);

        // enter new username
        WebElement usernameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("login"))
        );

        usernameInput.clear();
        usernameInput.sendKeys("softwaretestingusername123456");

        // trigger UI update
        ((JavascriptExecutor) driver).executeScript("arguments[0].blur();", usernameInput);
        driver.findElement(By.tagName("body")).click();

        sleep(PAUSE_LONG);

        // scope to correct form
        WebElement form = usernameInput.findElement(By.xpath("./ancestor::form"));
        WebElement changeBtn = form.findElement(By.cssSelector("button[type='submit']"));

        // click safely
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", changeBtn);

        sleep(PAUSE_LONG);

        // change username back afterwards

        driver.get(SETTINGS_URL);

        // click on account link
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Account")));
        accountLink.click();

        sleep(PAUSE_MEDIUM);

        // click change username
        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("dialog-show-rename-warning-dialog")
                )
        );

        changeUsernameBtn.click();

        sleep(PAUSE_MEDIUM);

        // click confirmation
        wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[data-show-dialog-id='rename-form-dialog']")
                )
        );
        confirmBtn.click();

        sleep(PAUSE_MEDIUM);

        // enter old username
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login")));

        usernameInput.clear();
        usernameInput.sendKeys("TestFinal777");

        // trigger UI update
        ((JavascriptExecutor) driver).executeScript("arguments[0].blur();", usernameInput);
        driver.findElement(By.tagName("body")).click();

        sleep(PAUSE_LONG);

        // click safely
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", changeBtn);

        sleep(PAUSE_LONG);


    }

    // ****************** Test 3 - Change Site Theme ******************
    @Test (priority = 3)
    public void changeTheme() throws InterruptedException{

        driver.get(SETTINGS_URL);

        // click on appearance link
        WebElement appearanceLink= wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Appearance"))
        );

        appearanceLink.click();
        sleep(PAUSE_MEDIUM);

        // open theme dropdown
        WebElement themeDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("color_mode_type_select"))
        );
        themeDropdown.click();
        sleep(PAUSE_MEDIUM);

        // select 'single'
        Select themeSelect = new Select(themeDropdown);
        themeSelect.selectByValue("single");
        sleep(PAUSE_MEDIUM);

        // click on dark theme
        WebElement darkDefault = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("option-dark"))
        );
        darkDefault.click();
        sleep(PAUSE_LONG);

        Assert.assertTrue(
                driver.getPageSource().toLowerCase().contains("dark"),
                "Theme did not change to dark"
        );

        // click on soft dark theme
        WebElement softDark = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("option-dark_dimmed"))
        );
        softDark.click();
        sleep(PAUSE_LONG);

    }

    // ****************** Test 4 - Appearance Settings ******************
    @Test (priority = 4)
    public void appearanceSettings() throws InterruptedException {

        driver.get(SETTINGS_URL);

        WebElement appearanceLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Appearance"))
        );

        appearanceLink.click();
        sleep(PAUSE_MEDIUM);

        JavascriptExecutor exe = (JavascriptExecutor) driver;

        // scroll down page
        exe.executeScript("window.scroll(0, 5000)", "");
        sleep(PAUSE_MEDIUM);

        // find toggle element
        WebElement toggle = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[aria-labelledby='increase-contrast-label']")
                )
        );

        // toggle ON
        toggle.click();
        sleep(PAUSE_MEDIUM);


        // verify ON
        Assert.assertEquals(toggle.getAttribute("aria-pressed"), "true");

        // toggle OFF
        toggle.click();

        sleep(PAUSE_MEDIUM);

        WebElement tabSizeDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("tab_size_rendering_preference"))
        );
        tabSizeDropdown.click();
        sleep(PAUSE_MEDIUM);

        // Select "Single theme"
        Select tabSize = new Select(tabSizeDropdown);
        tabSize.selectByValue("5");
        sleep(PAUSE_MEDIUM);

        tabSize.selectByValue("4");
        sleep(PAUSE_MEDIUM);


    }

    // ****************** Test 5 - Change Accessibility Features ******************
    @Test (priority = 5)
    public void changeAccessibilityFeatures() throws InterruptedException{
        driver.get(SETTINGS_URL);

        // click on accessibility link
        WebElement accessibilityLink= wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Accessibility"))
        );

        accessibilityLink.click();
        sleep(PAUSE_SHORT);


        // Keyboard Shortcuts
        WebElement keyboardCheckbox = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("keyboard_shortcuts_preference"))
        );
        keyboardCheckbox.click();
        sleep(PAUSE_SHORT);

        keyboardCheckbox.click();
        sleep(PAUSE_MEDIUM);

        // Motion Section
        driver.findElement(By.id("user_animated_images_enabled")).click();
        sleep(PAUSE_MEDIUM);

        driver.findElement(By.id("user_animated_images_disabled")).click();
        sleep(PAUSE_MEDIUM);

        driver.findElement(By.id("user_animated_images_system")).click();
        sleep(PAUSE_MEDIUM);

        JavascriptExecutor exe = (JavascriptExecutor) driver;

        // Link Underlines
        exe.executeScript("window.scroll(0, 500)","");
        sleep(PAUSE_SHORT);
        driver.findElement(By.id("user_link_underlines_false")).click();
        sleep(PAUSE_MEDIUM);

        driver.findElement(By.id("user_link_underlines_true")).click();
        sleep(PAUSE_MEDIUM);

        // Hovercards
        exe.executeScript("window.scroll(0, 500)","");
        sleep(PAUSE_SHORT);
        WebElement hovercardsCheckbox = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("hovercards_enabled"))
        );
        hovercardsCheckbox.click();
        sleep(PAUSE_MEDIUM);
        hovercardsCheckbox.click();
        sleep(PAUSE_MEDIUM);

        // URL Paste Behavior
        exe.executeScript("window.scroll(0, 500)","");
        sleep(PAUSE_SHORT);
        driver.findElement(By.id("user_paste_url_markdown_false")).click();
        sleep(PAUSE_MEDIUM);
        driver.findElement(By.id("user_paste_url_markdown_true")).click();
        sleep(PAUSE_MEDIUM);

        // Assistive technology hints
        driver.findElement((By.id("user_announcement_preference_hovercard_false"))).click();
        sleep(PAUSE_MEDIUM);
        driver.findElement(By.id("user_announcement_preference_hovercard_true")).click();
        sleep(PAUSE_MEDIUM);


    }

}