package com.softserve.academy;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.stream.Stream;

import static com.softserve.academy.MessagesEN.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GreenCityNegativeRegistrationTest {
    private static WebDriver driver;

    @BeforeAll
    static void setUp() {
        ChromeOptions options = new ChromeOptions();
//        FirefoxOptions options = new FirefoxOptions();
//        options.addPreference("intl.accept_languages", "en-GB, en");
        // Check if we are running in CI (GitHub Actions)
        if (System.getenv("GITHUB_ACTIONS") != null) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
        }

        driver = WebDriverManager.chromedriver().capabilities(options).create();
//        driver = WebDriverManager.firefoxdriver().capabilities(options).create();

        driver.manage().window().maximize();
        // At this stage, we are not using complex waits, so we just maximize the window
    }

    @BeforeEach
    void openRegistrationForm() throws InterruptedException {
        // 1. Open the main page
        driver.navigate().to("https://www.greencity.cx.ua/#/greenCity");

        // Bad practice: using a delay to allow the page to load completely.
        // This is necessary because the site may load slowly.
        Thread.sleep(1000);

        // 2. Click the "Sign Up" button to open the modal window
        driver.findElement(By.cssSelector(".header_sign-up-btn > span")).click();

        // Bad practice: using a delay to allow the modal window to open.
        Thread.sleep(1000);
    }


    @BeforeEach
    void openUrl() {
        driver.navigate().to("https://www.greencity.cx.ua/#/greenCity");
        sleep(1000);
    }

    @AfterEach
    void cleanUp() {
        driver.manage().deleteAllCookies();
        driver.navigate().refresh();
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

// Це використав як тест передачі параметрів через метод.
    static Stream<Arguments> registrationDataProviderWithNoParameters() {
        return Stream.of(
                Arguments.of("", "", "", ""),
                Arguments.of("", "", "", ""),
                Arguments.of("", "", "", ""),
                Arguments.of("", "", "", ""),
                Arguments.of("", "", "", ""),
                Arguments.of("", "", "", "")
        );
    }

    // --- TESTS ---



    //andrii.koval@gmail..com - цікаво що ця пошта помилку не дає

    @ParameterizedTest(name = "Email: {0}, Name:{1}, Password:{2}, ConfirmPassword:{3}")
    @DisplayName("Invalid email format (without @) → email error")
    @CsvFileSource(resources = "/invalid_emails.csv", numLinesToSkip = 1)
    void shouldShowErrorForInvalidEmail(String email, String name, String password, String confirmPassword) throws InterruptedException {

        typeEmail(email);
        typeUsername(name);
        typePassword(password);
        typeConfirm(confirmPassword);

        Thread.sleep(1000);

        assertEmailIstValidErrorVisible();
        assertSignUpButtonDisabled();
    }

    @ParameterizedTest
    @DisplayName("All fields invalid_names_isempty.csv → required errors shown")
    @MethodSource("registrationDataProviderWithNoParameters")
    void shouldShowErrorsForAllEmptyFields(String email, String name, String password) throws InterruptedException {

        typeEmail(email);
        typeUsername(name);
        typePassword(password);
        typeConfirm(password);
        Thread.sleep(1000);

        assertEmailEmptyErrorVisible();
        assertUsernameErrorVisible();
        assertPasswordWithSpaceAndNoSymbolErrorVisible();
        assertConfirmErrorVisible();

        clickSignUp();
    }


    @ParameterizedTest(name = "Email: {0}, Name:{1}, Password:{2}")
    @DisplayName("Empty username → username required")
    @CsvFileSource(resources = "/invalid_names_isempty.csv", numLinesToSkip = 1)
    void shouldShowErrorForEmptyUsername(String email, String name, String password) throws InterruptedException {

        typeEmail(email);
        typeUsername("");
        typePassword(password);
        typeConfirm(password);


        assertUsernameErrorVisible();
        clickSignUp();
    }

    @ParameterizedTest(name = "Email: {0}, Name:{1}, Password:{2}, ConfirmPassword:{3}")
    @DisplayName("Invalid username → username required")
    @CsvFileSource(resources = "/invalid_username.csv", numLinesToSkip = 1)
    void shouldShowErrorForInvalidUsername(String email, String name, String password, String confirmPassword) throws InterruptedException {

        typeEmail(email);
        typeUsername(name);
        typePassword(password);
        typeConfirm(confirmPassword);

        assertInvalidUserNameErrorVisible();
        clickSignUp();
    }

    @ParameterizedTest(name = "Email: {0}, Name:{1}, Password:{2}")
    @DisplayName("Short password (<8) → password rule error")
    @CsvFileSource(resources = "/invalid_passwords_tooshort.csv", numLinesToSkip = 1)
    void shouldShowErrorForShortPassword(String email, String name, String password) throws InterruptedException {

        typeEmail(email);
        typeUsername(name);
        typePassword(password);
        typeConfirm(password);

        assertPasswordWithSpaceAndNoSymbolErrorVisible();
        clickSignUp();
    }

    @ParameterizedTest(name = "Email: {0}, Name:{1}, Password:{2}")
    @DisplayName("Password with space → password rule error")
    @CsvFileSource(resources = "/invalid_paswword_with_space.csv", numLinesToSkip = 1)
    void shouldShowErrorForPasswordWithSpace(String email, String name, String password) throws InterruptedException {

        typeEmail(email);
        typeUsername(name);
        typePassword(password);

        assertPasswordWithSpaceAndNoSymbolErrorVisible();
        clickSignUp();
    }

    @ParameterizedTest(name = "Email: {0}, Name:{1}, Password:{2}, ConfirmPassword:{3}")
    @DisplayName("Confirm password mismatch → confirm error")
    @CsvFileSource(resources = "/password_mismatch.csv", numLinesToSkip = 1)
    void shouldShowErrorForPasswordMismatch(String email, String name, String password, String confirmPassword) throws InterruptedException {

        typeEmail(email);
        typeUsername(name);
        typePassword(password);
        typeConfirm(confirmPassword);

        assertMismatchPasswordsVisible();
        clickSignUp();
    }


    @ParameterizedTest(name = "Email: {0}, Name:{1} ConfirmPassword:{3}")
    @DisplayName("Confirm password mismatch → confirm error")
    @CsvFileSource(resources = "/valid_data.csv", numLinesToSkip = 1)
    void shouldShowErrorThatShouldInputePasswordBeforeConfirm(String email, String name, String confirmPassword) throws InterruptedException {

        typeEmail(email);
        typeUsername(name);
        typeConfirm(confirmPassword);

        assertConfirmFirsThenPasswordErrorVisible();
        clickSignUp();
    }


    // --- HELPERS (Helper methods) ---
    // This is the first step towards structuring code before learning Page Object

    private void typeEmail(String value) {
        WebElement field = driver.findElement(By.id("email"));
        field.clear();
        field.sendKeys(value);
    }

    private void typeUsername(String value) {
        WebElement field = driver.findElement(By.id("firstName"));
        field.clear();
        field.sendKeys(value);
    }

    private void typePassword(String value) {
        WebElement field = driver.findElement(By.id("password"));
        field.clear();
        field.sendKeys(value);
    }

    private void typeConfirm(String value) {
        WebElement field = driver.findElement(By.id("repeatPassword"));
        field.clear();
        field.sendKeys(value);
    }

    private void clickSignUp() {
        driver.findElement(By.cssSelector("button[type='submit'].greenStyle")).click();
    }

    private void assertEmailEmptyErrorVisible() {
        WebElement error = driver.findElement(By.id("email-err-msg"));
        assertTrue(error.isDisplayed(), "Email error message should be visible");
        String emailEmptyErrorMsg = EMAIL_REQUIRED.toLowerCase();
        assertTrue(error.getText().toLowerCase().contains("email is required.") || error.getText().toLowerCase().contains(emailEmptyErrorMsg));
        System.out.println("The email error message is visible");
    }

    private void assertEmailIstValidErrorVisible() {
        WebElement error = driver.findElement(By.id("email-err-msg"));
        assertTrue(error.isDisplayed(), "Email error message should be visible");
        String emailNotValidErrorMsg = EMAIL_INVALID.toLowerCase();
        assertTrue(error.getText().toLowerCase().contains("indicated correctly") || error.getText().toLowerCase().contains(emailNotValidErrorMsg));
        System.out.println("The email isn't valid error message is visible");
    }

    private void assertUsernameErrorVisible() {
        WebElement error = driver.findElement(By.id("firstname-err-msg"));
        assertTrue(error.isDisplayed(), "Email error message should be visible");
        String userNameEmptyErrorMsg = USERNAME_REQUIRED.toLowerCase();
        assertTrue(error.getText().toLowerCase().contains("check") || error.getText().toLowerCase().contains(userNameEmptyErrorMsg));
        System.out.println("The username error message is visible");
    }

    private void assertSignUpButtonDisabled() {
        WebElement btn = driver.findElement(By.cssSelector("button[type='submit'].greenStyle"));
        assertFalse(btn.isEnabled(), "The 'Sign Up' button should be disabled with invalid data");
    }


    private void assertPasswordWithSpaceAndNoSymbolErrorVisible() {
        WebElement error = driver.findElement(By.className("password-not-valid"));
        assertTrue(error.isDisplayed(), "Password error message should be visible");
        String passwordEmptyErrorMsg = PASSWORD_INVALID_RULES.toLowerCase();
        assertTrue(error.getText().toLowerCase().contains("8 to 20") || error.getText().toLowerCase().contains(passwordEmptyErrorMsg));
        System.out.println("The password error message is visible");
    }

    private void assertConfirmErrorVisible() {
        WebElement error = driver.findElement(By.id("confirm-err-msg"));
        assertTrue(error.isDisplayed(), "Confirm password error message should be visible");
        String confirmPasswordEmptyErrorMsg = CONFIRM_REQUIRED.toLowerCase();
        assertTrue(error.getText().toLowerCase().contains("required") || error.getText().toLowerCase().contains(confirmPasswordEmptyErrorMsg));
        System.out.println("The error message is visible");
    }


    private void assertMismatchPasswordsVisible() {
        WebElement error = driver.findElement(By.id("confirm-err-msg"));
        assertTrue(error.isDisplayed(), "Confirm password error message should be visible");
        String confirmPasswordErrorMsg = CONFIRM_MISMATCH.toLowerCase();
        assertTrue(error.getText().toLowerCase().contains("required") || error.getText().toLowerCase().contains(confirmPasswordErrorMsg));
        System.out.println("The error message that passwords are mismatch is visible");
    }

    private void assertConfirmFirsThenPasswordErrorVisible() {
        WebElement error = driver.findElement(By.id("confirm-err-msg"));
        assertTrue(error.isDisplayed(), "Confirm password error message should be visible");
        String confirmPasswordEmptyErrorMsg = CONFIRM_TYPE_PASS_FIRST.toLowerCase();
        assertTrue(error.getText().toLowerCase().contains("Please") || error.getText().toLowerCase().contains(confirmPasswordEmptyErrorMsg));
        System.out.println("The error message is visible");
    }

    private void assertInvalidUserNameErrorVisible() {
        WebElement error = driver.findElement(By.id("firstname-err-msg"));
        assertTrue(error.isDisplayed(), "Invalid user name error message should be visible");
        String invalidUserNameErrorMsg = USERNAME_INVALID_FORMAT.toLowerCase();
        assertTrue(error.getText().toLowerCase().contains("1-30 characters") || error.getText().toLowerCase().contains(invalidUserNameErrorMsg));
        System.out.println("The error message is visible");
    }


    @AfterAll
    static void tearDown() {
        if (driver != null) {
//            driver.quit();
            driver.navigate().refresh();

        }
    }
}
