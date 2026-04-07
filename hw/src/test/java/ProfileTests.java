import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;

public class ProfileTests {

    private WebDriver driver;
    private WebDriverWait wait;

    private void pause(long ms) {
        try { Thread.sleep(ms); } catch (Exception ignored) {}
    }

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-data-dir=C:\\GitHubProfile");
        options.addArguments("profile-directory=Profile1");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void updateEmails() {

        // Go to profile
        driver.get("https://github.com/STestacc");
        pause(1000);

        // Go to settings
        driver.get("https://github.com/settings/profile");
        pause(1000);

        // Go to emails
        driver.get("https://github.com/settings/emails");
        pause(1000);

        // ADD EMAIL
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")))
                .sendKeys("Enteruser@gmail.com");

        // Click the correct "Add" button
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Add')]")
        )).click();

        pause(2000);

        // DELETE EMAIL
        try {
            WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(text(),'ightje4@eamil.com')]/ancestor::div[contains(@class,'Box-row')]//button[contains(text(),'Remove')]")
            ));
            deleteBtn.click();
            pause(1000);
        } catch (Exception e) {
            System.out.println("Email not found — skipping delete.");
        }
    }
}
