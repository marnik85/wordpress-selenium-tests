package com.example.wordpress;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;

    @BeforeEach
    void setUp() {
        WebDriverManager.firefoxdriver().setup();

        //ChromeOptions options = new ChromeOptions();
        //options.addArguments("--start-maximized");

        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get("http://wordpresstestsite/wp-login.php");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
