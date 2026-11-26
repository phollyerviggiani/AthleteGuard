// browser testing the login page using playwright and junit5
package com.phollyerviggiani.athleteguard.e2e;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LoginPageTest {

    private static Playwright playwright;
    private static Browser browser;

    private BrowserContext context;
    private Page page;

    private final String athleteEmail = System.getenv("TEST_ATHLETE_EMAIL");
    private final String athletePassword = System.getenv("TEST_ATHLETE_PASSWORD");
    private final String coachEmail = System.getenv("TEST_COACH_EMAIL");
    private final String coachPassword = System.getenv("TEST_COACH_PASSWORD");

    private static final String BASE_URL = "http://localhost:8080/";
    private static final String HOME_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final Pattern ATHLETE_PATH = Pattern.compile(".*/athletes/\\d+$");

    @BeforeAll
    static void setUpBrowser() {
        playwright = Playwright.create();

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
        );
    }

    @BeforeEach
    void setUpPage() {
        context = browser.newContext(
                new Browser.NewContextOptions().setBaseURL(BASE_URL)
        );
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

        Response response = page.navigate(LOGIN_PATH);

        assertNotNull(response);
        assertEquals(200, response.status());

        assertThat(page).hasTitle("Login");
    }

    // just verifying elements are present on the login page
    @Test
    void shouldDisplayLoginFormElements() {

        page.navigate(LOGIN_PATH);

        // Verify page headings
        assertThat(page.locator("h1.login-heading")).isVisible();
        assertThat(page.locator("h2.login-title")).isVisible();

        // username/pw
        assertThat(page.locator("input[name='username']")).isVisible();
        assertThat(page.locator("input[name='password']")).isVisible();

        // button works
        assertThat(page.locator("button[type='submit']")).isVisible();
    }


    // Verify that the email and password inputs are required
    @Test
    void shouldRequireEmailAndPassword() {

        page.navigate(LOGIN_PATH);

        assertThat(page.locator("input[name='username']")).hasAttribute("required", "");
        assertThat(page.locator("input[name='password']")).hasAttribute("required", "");
    }


    // Verify that the form cannot be submitted with empty inputs
    @Test
    void shouldPreventEmptyFormSubmission() {

        page.navigate(LOGIN_PATH);

        page.locator("button[type='submit']").click();

        // Verify the browser remains on the login page
        assertThat(page).hasURL(LOGIN_PATH);

        // Verify both inputs remain empty
        assertThat(page.locator("input[name='username']")).hasValue("");
        assertThat(page.locator("input[name='password']")).hasValue("");
    }

    // reject invalid creds
    @Test
    void shouldDisplayErrorForInvalidCredentials() {

        page.navigate(LOGIN_PATH);

        // Enter invalid credentials
        page.locator("input[name='username']")
                .fill("invalid@example.com");

        page.locator("input[name='password']")
                .fill("WrongPassword123");

        // Submit login form
        page.locator("button[type='submit']").click();

        // Verify redirect to log in error page
        assertThat(page).hasURL(LOGIN_PATH + "?error");

        // Verify error message
        Locator errorMessage = page.locator(".alert-error");

        assertThat(errorMessage).isVisible();
        assertThat(errorMessage).hasText("Invalid email or password");
    }


    // valid login time athlete
    @Test
    void shouldSuccessfullyLoginAsAthlete() {

        page.navigate(LOGIN_PATH);

        // Enter valid athlete credentials
        loginAs(athleteEmail, athletePassword);

        assertThat(page).hasURL(ATHLETE_PATH);
    }


    // Verify that an authenticated athlete is redirected away from the login page
    @Test
    void shouldRedirectAuthenticatedAthleteAwayFromLoginPage() {
        page.navigate(LOGIN_PATH);

        // Log in as an athlete
        loginAs(athleteEmail, athletePassword);

        assertThat(page).hasURL(ATHLETE_PATH);

        // Store the athlete's dashboard URL
        String athleteDashboardUrl = page.url();

        // Attempt to revisit the login page while authenticated
        page.navigate(LOGIN_PATH);

        // Verify the athlete is redirected back to their dashboard
        assertThat(page).hasURL(athleteDashboardUrl);

    }

    // valid login time coach
    @Test
    void shouldSuccessfullyLoginAsCoach() {

        page.navigate(LOGIN_PATH);

        loginAs(coachEmail, coachPassword);

        // make sure auth redirect to dashboard
        assertThat(page).hasURL(HOME_PATH);

    }

    // verify that logout works for athlete and that they cannot access their dashboard after logging out
    @Test
    void shouldSuccessfullyLogoutAsAthlete() {
        page.navigate(LOGIN_PATH);

        // Log in as an athlete
        loginAs(athleteEmail, athletePassword);

        assertThat(page).hasURL(ATHLETE_PATH);

        // Store the athlete's dashboard URL
        String athleteDashboardUrl = page.url();

        // Click the logout button in the navbar
        page.locator(".btn-navbar-logout").click();

        // Verify successful logout redirect
        assertThat(page).hasURL(LOGIN_PATH + "?logout");

        // Verify logout confirmation message
        Locator successMessage = page.locator(".alert-success");

        assertThat(successMessage).isVisible();
        assertThat(successMessage).hasText("Logged out successfully");

        // Attempt to revisit the athlete dashboard
        page.navigate(athleteDashboardUrl);

        // Verify access is denied and the athlete is redirected to log in
        assertThat(page).hasURL(LOGIN_PATH );
        assertThat(page.locator("input[name='username']")).isVisible();
    }

    // login helper method
    private void loginAs(String email, String password) {
        page.navigate(LOGIN_PATH);
        page.locator("input[name='username']").fill(email);
        page.locator("input[name='password']").fill(password);
        page.locator("button[type='submit']").click();
    }

}
