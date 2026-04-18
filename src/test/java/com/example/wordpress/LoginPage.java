package com.example.wordpress;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.TimeoutException;
import java.time.Duration;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By usernameField = By.id("user_login");
    private final By passwordField = By.id("user_pass");
    private final By loginButton = By.id("wp-submit");
    private final By dashboardMarker = By.id("wpadminbar");
    private final By profileMenu = By.id("wp-admin-bar-my-account");
    private final By logoutLink = By.xpath("//a[contains(@href, 'wp-login.php?action=logout')]");
    private final By loginError = By.id("login_error");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open(String loginUrl) {
        driver.get(loginUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
    }

    public void login(String username, String password) {
        driver.findElement(usernameField).clear();
        driver.findElement(usernameField).sendKeys(username);

        driver.findElement(passwordField).clear();
        driver.findElement(passwordField).sendKeys(password);

        driver.findElement(loginButton).click();
    }

    public boolean isLoggedIn() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardMarker));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLoginErrorDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(loginError)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void logout() {
        WebElement profile = wait.until(ExpectedConditions.visibilityOfElementLocated(profileMenu));

        Actions actions = new Actions(driver);
        actions.moveToElement(profile).perform();

        WebElement logout = wait.until(ExpectedConditions.elementToBeClickable(logoutLink));
        logout.click();
    }

    public boolean isLoggedOut() {
        try {
            wait.until(ExpectedConditions.urlContains("wp-login.php?loggedout"));
            return driver.getCurrentUrl().contains("wp-login.php?loggedout");
        } catch (Exception e) {
            return false;
        }
    }

    public String getLoginErrorText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(loginError)).getText();
        } catch (TimeoutException e) {
            return "";
        }
    }

    public boolean isBrowserValidationDisplayed() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement username = driver.findElement(usernameField);
            WebElement password = driver.findElement(passwordField);
            String usernameMsg = (String) js.executeScript("return arguments[0].validationMessage;", username);
            String passwordMsg = (String) js.executeScript("return arguments[0].validationMessage;", password);
            return (usernameMsg != null && !usernameMsg.isEmpty()) || (passwordMsg != null && !passwordMsg.isEmpty());
        } catch (Exception e) {
            System.err.println("isBrowserValidationDisplayed failed: " + e.getMessage());
            return false;
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}