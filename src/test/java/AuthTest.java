import com.codeborne.selenide.Condition;
import data.DataGenerator;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.url;
import static data.DataGenerator.Registration.getRegisteredUser;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthTest {
    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
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
        webdriver().shouldHave(url("http://localhost:9999/dashboard"));
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