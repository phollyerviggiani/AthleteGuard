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


    // valid login time athlete
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

    }


    // Verify that an authenticated athlete is redirected away from the login page
    @Test
    void shouldRedirectAuthenticatedAthleteAwayFromLoginPage() {
        page.navigate("http://localhost:8080/login");

        // Log in as an athlete
        page.locator("input[name='username']")
                .fill(athleteEmail);

        page.locator("input[name='password']")
                .fill(athletePassword);

        page.locator("button[type='submit']").click();

        // Store the athlete's dashboard URL
        String athleteDashboardUrl = page.url();

        assertTrue(
                athleteDashboardUrl.matches("http://localhost:8080/athletes/\\d+"),
                "Athlete should initially be redirected to their dashboard"
        );

        // Attempt to revisit the login page while authenticated
        page.navigate("http://localhost:8080/login");

        // Verify the athlete is redirected back to their dashboard
        assertEquals(
                athleteDashboardUrl,
                page.url(),
                "Authenticated athlete should be redirected to their own dashboard"
        );
    }

    // valid login time coach
    @Test
    void shouldSuccessfullyLoginAsCoach() {

        page.navigate("http://localhost:8080/login");

        page.locator("input[name='username']")
                .fill(coachEmail);

        page.locator("input[name='password']")
                .fill(coachPassword);

        page.locator("button[type='submit']").click();

        // make sure auth redirect to dashboard
        assertTrue(
                page.url().matches("http://localhost:8080/"),
                "Coach should be redirected to the home page after successful login"
        );

    }

    // verify that logout works for athlete and that they cannot access their dashboard after logging out
    @Test
    void shouldSuccessfullyLogoutAsAthlete() {
        page.navigate("http://localhost:8080/login");

        // Log in as an athlete
        page.locator("input[name='username']")
                .fill(athleteEmail);

        page.locator("input[name='password']")
                .fill(athletePassword);

        page.locator("button[type='submit']").click();

        // Store the athlete's dashboard URL
        String athleteDashboardUrl = page.url();

        assertTrue(
                athleteDashboardUrl.matches("http://localhost:8080/athletes/\\d+")
        );

        // Click the logout button in the navbar
        page.locator(".btn-navbar-logout").click();

        // Verify successful logout redirect
        assertTrue(
                page.url().endsWith("/login?logout"),
                "Athlete should be redirected to the login page after logout"
        );

        // Verify logout confirmation message
        Locator successMessage = page.locator(".alert-success");

        assertTrue(successMessage.isVisible());

        assertEquals(
                "Logged out successfully",
                successMessage.textContent().trim()
        );

        // Attempt to revisit the athlete dashboard
        page.navigate(athleteDashboardUrl);

        // Verify access is denied and the athlete is redirected to login
        assertTrue(
                page.url().startsWith("http://localhost:8080/login"),
                "Logged-out athlete should not be able to access their dashboard"
        );

        assertTrue(page.locator("input[name='username']").isVisible());
    }

}
