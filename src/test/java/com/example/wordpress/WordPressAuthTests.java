package com.example.wordpress;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class WordPressAuthTests extends BaseTest {

    private static final String WORDPRESS_LOGIN_URL = "https://wordpresstestsite/wp-login.php";

    static Stream<UserCredentials> wordpressUsers() {
        return Stream.of(
                new UserCredentials("sub1", "12345"),
                new UserCredentials("sub2", "12345"),
                new UserCredentials("sub3", "12345"),
                new UserCredentials("editor1", "12345"),
                new UserCredentials("contributor1", "12345"),
                new UserCredentials("author1", "12345"),
                new UserCredentials("admin", "12345")
        );
    }

    @ParameterizedTest(name = "Login and logout test for user: {0}")
    @MethodSource("wordpressUsers")
    void shouldLoginAndLogoutSuccessfullyForEachUser(UserCredentials user) {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.open(WORDPRESS_LOGIN_URL);
        loginPage.login(user.username(), user.password());

        Assertions.assertTrue(
                loginPage.isLoggedIn(),
                "Expected user to log in successfully: " + user.username()
        );

        String currentUrl = loginPage.getCurrentUrl();
        Assertions.assertTrue(
                currentUrl.contains("wp-admin"),
                "Expected to be redirected to admin area after login, but was: " + currentUrl
        );

        loginPage.logout();

        Assertions.assertTrue(
                loginPage.isLoggedOut(),
                "Expected user to log out successfully: " + user.username()
        );
    }

    @Nested
    class WordPressNegativeTests {

        private LoginPage loginPage;

        @BeforeEach
        void setUpLoginPage() {
            loginPage = new LoginPage(driver);
            loginPage.open(WORDPRESS_LOGIN_URL);
        }

        @Test
        void shouldShowErrorForInvalidLogin() {
            loginPage.login("wrong_user", "wrong_password");

            Assertions.assertTrue(
                    loginPage.isLoginErrorDisplayed(),
                    "Expected a login error to be displayed for invalid credentials"
            );
        }

        @Test
        void shouldShowErrorForEmptyCredentials() {
            loginPage.login("", "");

            Assertions.assertTrue(
                    loginPage.isLoginErrorDisplayed(),
                    "Expected a login error to be displayed when both fields are empty"
            );
        }

        @Test
        void shouldShowErrorForEmptyPassword() {
            loginPage.login("admin", "");

            Assertions.assertTrue(
                    loginPage.isLoginErrorDisplayed(),
                    "Expected a login error to be displayed when password is empty"
            );
        }

        @Test
        void shouldShowErrorForEmptyUsername() {
            loginPage.login("", "12345");

            Assertions.assertTrue(
                    loginPage.isLoginErrorDisplayed(),
                    "Expected a login error to be displayed when username is empty"
            );
        }

        @Test
        void shouldShowErrorForWrongPasswordWithValidUser() {
            loginPage.login("admin", "wrong_password");

            Assertions.assertTrue(
                    loginPage.isLoginErrorDisplayed(),
                    "Expected a login error to be displayed for wrong password"
            );
            Assertions.assertFalse(
                    loginPage.isLoggedIn(),
                    "Expected user NOT to be logged in after wrong password"
            );
        }

        @Test
        void shouldShowErrorForNonExistentUser() {
            loginPage.login("nonexistent_user_xyz", "12345");

            Assertions.assertTrue(
                    loginPage.isLoginErrorDisplayed(),
                    "Expected a login error to be displayed for a non-existent user"
            );
        }

        @Test
        void shouldContainErrorTextForInvalidCredentials() {
            loginPage.login("wrong_user", "wrong_password");

            String errorText = loginPage.getLoginErrorText();
            Assertions.assertFalse(
                    errorText.isEmpty(),
                    "Expected a non-empty error message for invalid credentials"
            );
        }

        @Test
        void shouldRemainOnLoginPageAfterFailedLogin() {
            loginPage.login("wrong_user", "wrong_password");

            String currentUrl = loginPage.getCurrentUrl();
            Assertions.assertTrue(
                    currentUrl.contains("wp-login.php"),
                    "Expected to remain on the login page after a failed login attempt, but was: " + currentUrl
            );
        }
    }

    record UserCredentials(String username, String password) {
        @Override
        public String toString() {
            return username;
        }
    }
}
