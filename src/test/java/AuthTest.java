import com.codeborne.selenide.Condition;
import data.DataGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static data.DataGenerator.Registration.getRegisteredUser;

class AuthTest {
    public static final String NONEXISTENT = "NONEXISTENT";
    private static DataGenerator.RegistrationDto activeUser;
    private static DataGenerator.RegistrationDto blockedUser;

    @BeforeAll
    static void setUpAll() {
        activeUser = getRegisteredUser("active");
        blockedUser = getRegisteredUser("blocked");
        open("http://localhost:9999");
    }

    @AfterEach
    void reopenPage() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successfully login with active user")
    void shouldSuccessfullyLogin() {
        $(By.cssSelector("[name='login']")).sendKeys(activeUser.getLogin());
        $(By.cssSelector("[name='password']")).sendKeys(activeUser.getPassword());
        $(By.cssSelector("[data-test-id='action-login']")).click();
        $("h2")
                .shouldHave(Condition.text("Личный кабинет"))
                .shouldBe(Condition.visible);
    }

    @Test
    @DisplayName("Should not login with not existed user")
    void shouldNotLoginWithInvalidUser() {
        $(By.cssSelector("[name='login']")).sendKeys(NONEXISTENT);
        $(By.cssSelector("[name='password']")).sendKeys(NONEXISTENT);
        $(By.cssSelector("[data-test-id='action-login']")).click();
        $("[class='notification__content']")
                .shouldHave(Condition.text("Ошибка! Неверно указан логин или пароль"))
                .shouldBe(Condition.visible);
    }

    @Test
    @DisplayName("Should not login with invalid password")
    void shouldNotLoginWithInvalidPassword() {
        $(By.cssSelector("[name='login']")).sendKeys(activeUser.getLogin());
        $(By.cssSelector("[name='password']")).sendKeys(NONEXISTENT);
        $(By.cssSelector("[data-test-id='action-login']")).click();
        $("[class='notification__content']")
                .shouldHave(Condition.text("Ошибка! Неверно указан логин или пароль"))
                .shouldBe(Condition.visible);
    }

    @Test
    @DisplayName("Should not login with blocked user")
    void shouldNotLoginWithBlockedUser() {
        $(By.cssSelector("[name='login']")).sendKeys(blockedUser.getLogin());
        $(By.cssSelector("[name='password']")).sendKeys(blockedUser.getPassword());
        $(By.cssSelector("[data-test-id='action-login']")).click();
        $("[class='notification__content']")
                .shouldHave(Condition.text("Ошибка! Пользователь заблокирован"))
                .shouldBe(Condition.visible);
    }
}