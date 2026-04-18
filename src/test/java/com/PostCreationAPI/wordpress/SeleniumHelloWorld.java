package com.PostCreationAPI.wordpress;

import org.junit.jupiter.api.Test;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterEach;

public class SeleniumHelloWorld {
    WebDriver driver;
    String baseUrl = "http://wordpresstestsite/";

    @BeforeEach
    public void setUp(){
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        driver.get(baseUrl+"/wp-login.php");
    }

    @AfterEach
    public void tearDown(){
        driver.quit();
    }


    @Test
    public void adminLoginTest() {
        String username = "admin";
        String password = "12345";
        WebElement usernameField = driver.findElement(By.id("user_login"));
        usernameField.sendKeys(username);
        WebElement passwordField = driver.findElement(By.id("user_pass"));
        passwordField.sendKeys(password);
        WebElement loginButton = driver.findElement(By.id("wp-submit"));
        loginButton.click();
        String pageTitleString = driver.getTitle();
        System.out.println(pageTitleString);
        Assertions.assertEquals("Dashboard ‹ WordPress Test Site — WordPress", pageTitleString);
    }

    @Test
    public void createContentTest(){
        String username = "admin";
        String password = "12345";
        WebElement usernameField = driver.findElement(By.id("user_login"));
        usernameField.sendKeys(username);
        WebElement passwordField = driver.findElement(By.id("user_pass"));
        passwordField.sendKeys(password);
        WebElement loginButton = driver.findElement(By.id("wp-submit"));
        loginButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Posts"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Add Post"))).click();

        // Wait for the editor shell to load
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".edit-post-layout")
        ));

        // Switch to iframe if present
        List<WebElement> frames = driver.findElements(By.tagName("iframe"));
        if (!frames.isEmpty()) {
            driver.switchTo().frame(frames.get(0));
        }


        WebElement titleField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".editor-post-title__input")
        ));
        titleField.sendKeys("My automated title");

        // Locate any element whose aria-label contains "Empty block"
        WebElement emptyBlock = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("[aria-label*='Add default block']")
        ));

        Actions actions = new Actions(driver);
        actions.moveToElement(emptyBlock)
                .click()
                .sendKeys("This is my automated post body.")
                .perform();

        // switch back to main document if we switched to an iframe
        if (!frames.isEmpty()) {
            driver.switchTo().defaultContent();
        }

        // click the publish button and confirm the publish action
        WebElement publishPanelToggle = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.editor-post-publish-panel__toggle")
        ));
        publishPanelToggle.click();

        WebElement confirmPublishButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.editor-post-publish-button")
        ));
        confirmPublishButton.click();

        // wait for the publish success notice and assert it confirms publication.
        WebElement snackbarNotice = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".components-snackbar__content")
        ));
        Assertions.assertTrue(
                snackbarNotice.getText().toLowerCase().contains("published"),
                "Expected publish confirmation message, but got: " + snackbarNotice.getText()
        );
    }
}
