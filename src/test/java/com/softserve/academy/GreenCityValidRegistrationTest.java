package com.softserve.academy;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GreenCityValidRegistrationTest {

    private static WebDriver driver;

    @BeforeAll
    static void setUp() {
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("intl.accept_languages", "en-GB, en");
        driver = WebDriverManager.firefoxdriver().capabilities(options).create();

        driver.manage().window().maximize();
    }

    @BeforeEach
    void openUrl() {
        driver.navigate().to("https://www.greencity.cx.ua/#/greenCity");
        sleep(1000);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void registration(String email, String userName, String password) {
        driver.findElement(By.cssSelector(".header_sign-up-btn > span")).click();
        sleep(500);

        WebElement emailInput = driver.findElement(By.id("email"));
        emailInput.clear();
        emailInput.sendKeys(email);

        WebElement userNameInput = driver.findElement(By.id("firstName"));
        userNameInput.clear();
        userNameInput.sendKeys(userName);

        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.clear();
        passwordInput.sendKeys(password);

        WebElement confirmPasswordInput = driver.findElement(By.id("repeatPassword"));
        confirmPasswordInput.clear();
        confirmPasswordInput.sendKeys(password);

        WebElement btnSubmit = driver.findElement(By.cssSelector(".greenStyle"));
        if (btnSubmit.isEnabled()) {
            btnSubmit.click();
        }
        sleep(1000);
        String msg = driver.findElement(By.cssSelector(".mdc-snackbar__label")).getText();
        String expectedMsg = "successfully registered";

        assertTrue(msg.contains(expectedMsg), "The message should contain the substring: " + expectedMsg);

    }

    //@ValueSourse
    @ParameterizedTest
    @DisplayName("Check registration with different names with @ValueSource")
    @ValueSource(strings = {"Anna User", "Bob Green", "User123", "ANNA568", "User1234", "User1235", "User1236"})
    void testWithValueSource(String userName) {
        String email = "testData" + System.currentTimeMillis() + "@gmail.com";
        registration(email, userName, "TestPass12345%");
    }


    //@CsvSourse
    @ParameterizedTest(name = "Email: {0}, Name:{1}")
    @DisplayName("Check registration via CsvSourse")
    @CsvSource({
            "test_csv1@gmail.com, Ivan Brown, PassTest123%",
            "test_csv2@gmail.com, User123, PassTest1234%"

    })
    void testWithCsvSource(String email, String userName, String password) {
        String uniqueEmail = System.currentTimeMillis() + email;
        registration(uniqueEmail, userName, password);
    }

    //@CsvFileSourse
    @ParameterizedTest(name = "Data from file- Email:{0}")
    @DisplayName("Check registration via CsvFileSourse (external file")
    @CsvFileSource(resources = "/registration_data.csv", numLinesToSkip = 1)
    void testWithCsvFileSource(String email, String userName, String password) {
        String uniqueEmail = "from_file" + System.currentTimeMillis() + email;
    }




//    private void registration(String email, String userName, String password) {
//
//        driver.findElement(By.cssSelector(".header_sign-up-btn > span")).click();
//
//        sleep(1000);
//
//        WebElement emailInput = driver.findElement(By.id("email"));
//        emailInput.clear();
//        emailInput.sendKeys(email);
//
//        WebElement userNameInput = driver.findElement(By.id("firstName"));
//        userNameInput.clear();
//        userNameInput.sendKeys(userName);
//
//
//        WebElement passwordInput = driver.findElement(By.id("password"));
//        passwordInput.clear();
//        passwordInput.sendKeys(password);
//
//        WebElement confirmPasswordInput = driver.findElement(By.id("repeatPassword"));
//        confirmPasswordInput.clear();
//        confirmPasswordInput.sendKeys(password);
//
//        WebElement btnSubmit = driver.findElement(By.cssSelector(".greenStyle"));
//
//        if (btnSubmit.isEnabled()) {
//            btnSubmit.click();
//        }
//        sleep(1000);
//
//        String msg = driver.findElement(By.cssSelector(".mdc-snackbar__label")).getText();
//        String expectedMsgPart = "successfully registered";
//
//        assertTrue(msg.contains(expectedMsgPart), "Message should contain the substring: " + expectedMsgPart);
//
//    }

    @ParameterizedTest(name = "Email: {0}, Name:{1}, Password:{2}")
    @DisplayName("Check registration via CsvFileSourse (external file")
    @CsvFileSource(resources = "/test_users.csv", numLinesToSkip = 1)
    void registrationTest(String Email, String Name, String Password) {

    }
}