// browser testing the login page using playwright and junit5
package com.phollyerviggiani.athleteguard.e2e;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import com.phollyerviggiani.athleteguard.pages.LoginPage;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LoginPageTest {

    private static Playwright playwright;
    private static Browser browser;

    private BrowserContext context;
    private Page page;

    private LoginPage loginPage;

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
        loginPage = new LoginPage(page);
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

    // Checking if the page loads correctly and has the expected title
    @Test
    void shouldLoadLoginPage() {

        Response response = page.navigate(LOGIN_PATH);

        assertNotNull(response);
        assertEquals(200, response.status());

        assertThat(page).hasTitle("Login");
    }

    // Verifying elements are present on the login page
    @Test
    void shouldDisplayLoginFormElements() {

        page.navigate(LOGIN_PATH);
        loginPage.verifyLoginFormElements();
    }


    // Verify that the email and password inputs are required
    @Test
    void shouldRequireEmailAndPassword() {

        page.navigate(LOGIN_PATH);
        loginPage.verifyLoginFormElementsAreRequired();
    }


    // Verify that the form cannot be submitted with empty inputs
    @Test
    void shouldPreventEmptyFormSubmission() {

        page.navigate(LOGIN_PATH);
        loginPage.clickLogin();

        // Verify that the page remains on the login page and the inputs are still empty
        assertThat(page).hasURL(LOGIN_PATH);
        loginPage.verifyLoginFieldsAreEmpty();
    }

    // reject invalid creds
    @Test
    void shouldDisplayErrorForInvalidCredentials() {

        page.navigate(LOGIN_PATH);
        loginPage.invalidLogin();

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

        // Navigate to the login page, enter valid athlete credentials, and submit the form
        page.navigate(LOGIN_PATH);
        loginPage.loginAs(athleteEmail, athletePassword);

        assertThat(page).hasURL(ATHLETE_PATH);
    }


    // Verify that an authenticated athlete is redirected away from the login page
    @Test
    void shouldRedirectAuthenticatedAthleteAwayFromLoginPage() {
        page.navigate(LOGIN_PATH);
        loginPage.loginAs(athleteEmail, athletePassword);

        assertThat(page).hasURL(ATHLETE_PATH);

        // Store the athlete's dashboard URL
        String athleteDashboardUrl = page.url();
        page.navigate(LOGIN_PATH);

        // Verify the athlete is redirected back to their dashboard
        assertThat(page).hasURL(athleteDashboardUrl);

    }

    // valid login time coach
    @Test
    void shouldSuccessfullyLoginAsCoach() {

        page.navigate(LOGIN_PATH);
        loginPage.loginAs(coachEmail, coachPassword);

        // make sure auth redirect to dashboard
        assertThat(page).hasURL(HOME_PATH);

    }

    // verify that logout works for athlete and that they cannot access their dashboard after logging out
    @Test
    void shouldSuccessfullyLogoutAsAthlete() {
        page.navigate(LOGIN_PATH);
        loginPage.loginAs(athleteEmail, athletePassword);

        assertThat(page).hasURL(ATHLETE_PATH);

        // Store the athlete's dashboard URL
        String athleteDashboardUrl = page.url();

        // Click the logout button in the navbar
        loginPage.clickLogout();

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

    // ***** login helper method is now moved to the LoginPage class, so we can remove it from here *****
}
