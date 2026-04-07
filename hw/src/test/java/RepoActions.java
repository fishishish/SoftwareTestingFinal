import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;

public class RepoActionTests {

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
    public void starAndForkRepos() {

        driver.get("https://github.com/STestacc?tab=repositories");
        pause(1000);

        // STAR FIRST REPO
        WebElement starBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[contains(@aria-label,'Star')])[1]")
        ));
        starBtn.click();
        pause(1000);

        // OPEN SECOND REPO
        WebElement secondRepo = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//a[@itemprop='name codeRepository'])[2]")
        ));
        secondRepo.click();
        pause(1000);

        // FORK
        WebElement forkBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[contains(text(),'Fork')]")
        ));
        forkBtn.click();
        pause(2000);
    }
}
