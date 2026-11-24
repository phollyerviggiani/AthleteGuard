// browser testing the login page using playwright and junit5
package com.phollyerviggiani.athleteguard.e2e;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class LoginPageTest {

    private static Playwright playwright;
    private static Browser browser;

    private BrowserContext context;
    private Page page;

    private String athleteEmail = System.getenv("TEST_ATHLETE_EMAIL");
    private String athletePassword = System.getenv("TEST_ATHLETE_PASSWORD");
    private String coachEmail = System.getenv("TEST_COACH_EMAIL");
    private String coachPassword = System.getenv("TEST_COACH_PASSWORD");

    @BeforeAll
    static void setUpBrowser() {
        playwright = Playwright.create();

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
        );
    }

    @BeforeEach
    void setUpPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    @AfterAll
    static void closeBrowser() {
        browser.close();
        playwright.close();
    }

    // does it load
    @Test
    void shouldLoadLoginPage() {

        Response response = page.navigate("http://localhost:8080/login");

        assertNotNull(response);
        assertEquals(200, response.status());

        assertEquals("Login", page.title());
    }

    // just verifying elements are present on the login page
    @Test
    void shouldDisplayLoginFormElements() {

        page.navigate("http://localhost:8080/login");

        // Verify page headings
        assertTrue(page.locator("h1.login-heading").isVisible());
        assertTrue(page.locator("h2.login-title").isVisible());

        // Verify email and password inputs
        assertTrue(page.locator("input[name='username']").isVisible());
        assertTrue(page.locator("input[name='password']").isVisible());

        // Verify login button
        assertTrue(page.locator("button[type='submit']").isVisible());
    }


    // Verify that the email and password inputs are required
    @Test
    void shouldRequireEmailAndPassword() {

        page.navigate("http://localhost:8080/login");

        Locator emailInput = page.locator("input[name='username']");
        Locator passwordInput = page.locator("input[name='password']");

        assertTrue(emailInput.getAttribute("required") != null);
        assertTrue(passwordInput.getAttribute("required") != null);
    }


    // Verify that the form cannot be submitted with empty inputs
    @Test
    void shouldPreventEmptyFormSubmission() {

        page.navigate("http://localhost:8080/login");

        page.locator("button[type='submit']").click();

        // Verify the browser remains on the login page
        assertTrue(page.url().endsWith("/login"));

        // Verify both inputs remain empty
        assertEquals("", page.locator("input[name='username']").inputValue());
        assertEquals("", page.locator("input[name='password']").inputValue());
    }



    // reject invalid creds
    @Test
    void shouldDisplayErrorForInvalidCredentials() {

        page.navigate("http://localhost:8080/login");

        // Enter invalid credentials
        page.locator("input[name='username']")
                .fill("invalid@example.com");

        page.locator("input[name='password']")
                .fill("WrongPassword123");

        // Submit login form
        page.locator("button[type='submit']").click();

        // Verify redirect to log in error page
        assertTrue(page.url().contains("/login?error"));

        // Verify error message
        Locator errorMessage = page.locator(".alert-error");

        assertTrue(errorMessage.isVisible());

        assertEquals(
                "Invalid email or password",
                errorMessage.textContent().trim()
        );
    }


    // valid login time
    @Test
    void shouldSuccessfullyLoginAsAthlete() {

        page.navigate("http://localhost:8080/login");

        // Enter valid athlete credentials
        page.locator("input[name='username']")
                .fill(athleteEmail);

        page.locator("input[name='password']")
                .fill(athletePassword);

        // Submit login form
        page.locator("button[type='submit']").click();

        // Verify successful authentication and athlete dashboard redirect
        assertTrue(
                page.url().matches("http://localhost:8080/athletes/\\d+"),
                "Athlete should be redirected to their own dashboard"
        );

        page.waitForTimeout(3000);
    }


}
