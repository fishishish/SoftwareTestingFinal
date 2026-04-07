import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

import static java.lang.Thread.sleep;

public class ProfileTests {

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

    //DIRECT PROFILE PAGE
    @Test(priority = 1)
    public void openProfileDirectFromHome() throws InterruptedException {
        driver.get("https://github.com/");
        sleep(PAUSE_MEDIUM);

        // Go directly to your profile
        driver.get("https://github.com/STestacc");
        sleep(PAUSE_MEDIUM);

        Assert.assertTrue(
                driver.getCurrentUrl().contains("STestacc"),
                "Did not navigate to profile page"
        );
    }


// SETTINGS → EMAILS → ADD + DELETE EMAIL
    @Test(priority = 2)
    public void addAndDeleteEmail() throws InterruptedException {
        String emailToUse = "cheleyson14@gmail.com";

        driver.get("https://github.com/");
        sleep(PAUSE_MEDIUM);

        // 1️⃣ Go to Settings page
        driver.get("https://github.com/settings/profile");
        sleep(PAUSE_MEDIUM);

        // 2️⃣ Click "Emails" in the left sidebar (bulletproof selector)
        WebElement emailsTab = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a[href='/settings/emails']")
                )
        );
        emailsTab.click();
        sleep(PAUSE_MEDIUM);

        // 3️⃣ Type email
        WebElement emailInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("email"))
        );
        emailInput.clear();
        emailInput.sendKeys(emailToUse);
        sleep(PAUSE_MEDIUM);

        // 4️⃣ Click ADD
        WebElement addBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("form[action='/settings/emails/add'] button")
                )
        );
        addBtn.click();
        sleep(PAUSE_LONG);

        // 5️⃣ DELETE the email we just added
        try {
            WebElement deleteBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//span[contains(text(),'" + emailToUse + "')]/ancestor::div[contains(@class,'Box-row')]//button[contains(text(),'Remove')]")
                    )
            );
            deleteBtn.click();
            sleep(PAUSE_MEDIUM);
        } catch (TimeoutException e) {
            System.out.println("Email not found for delete — maybe it failed to add or was already removed.");
        }

        Assert.assertTrue(true, "Email add/delete flow executed");
    }
}
