// POM model for the login page of the Athlete Guard application
package com.phollyerviggiani.athleteguard.pages;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginPage {

    private final Page page;

    public LoginPage(Page page) {
        this.page = page;
    }

    public void verifyLoginFormElements() {
        assertThat(page.locator("h1.login-heading")).isVisible();
        assertThat(page.locator("h2.login-title")).isVisible();

        assertThat(page.locator("input[name='username']")).isVisible();
        assertThat(page.locator("input[name='password']")).isVisible();

        assertThat(page.locator("button[type='submit']")).isVisible();
    }

    public void verifyLoginFormElementsAreRequired() {
        assertThat(page.locator("input[name='username']")).hasAttribute("required", "");
        assertThat(page.locator("input[name='password']")).hasAttribute("required", "");
    }

    public void verifyLoginFieldsAreEmpty() {
        // Verify both inputs remain empty
        assertThat(page.locator("input[name='username']")).hasValue("");
        assertThat(page.locator("input[name='password']")).hasValue("");
    }

    public void enterEmail(String email) {
        page.locator("input[name='username']").fill(email);
    }

    public void enterPassword(String password) {
        page.locator("input[name='password']").fill(password);
    }

    public void clickLogin() {
        page.locator("button[type='submit']").click();
    }

    public void clickLogout() {
        page.locator(".btn-navbar-logout").click();
    }


    public void invalidLogin() {
        String invalidEmail = "thisisbad@example.com";
        enterEmail(invalidEmail);

        String invalidPassword = "thisisbadpassword";
        enterPassword(invalidPassword);
        clickLogin();
    }

    public void loginAs(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLogin();
    }
}
