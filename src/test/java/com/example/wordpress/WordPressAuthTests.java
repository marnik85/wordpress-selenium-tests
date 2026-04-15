package com.example.wordpress;

import org.junit.jupiter.api.Assertions;
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

        loginPage.logout();

        Assertions.assertTrue(
                loginPage.isLoggedOut(),
                "Expected user to log out successfully: " + user.username()
        );
    }

    record UserCredentials(String username, String password) {
        @Override
        public String toString() {
            return username;
        }


        @Nested
        class WordPressNegativeTests extends BaseTest {

            private static final String WORDPRESS_LOGIN_URL = "https://wordpresstestsite/wp-login.php";

            @Test
            void shouldShowErrorForInvalidLogin() {
                LoginPage loginPage = new LoginPage(driver);

                loginPage.open(WORDPRESS_LOGIN_URL);
                loginPage.login("wrong_user", "wrong_password");

                Assertions.assertTrue(loginPage.isLoginErrorDisplayed());
            }
        }
    }
}
