import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.Selenide.*;

public class ParametrizedTests {

  @BeforeAll
  static void beforeALL() {
    pageLoadStrategy = "eager";
    browserSize = "1024x768";
    browser = "chrome";
    ChromeOptions options = new ChromeOptions();
    options.addArguments("incognito");
    browserCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
  }

  @DisplayName("Проверка авторизации под разными пользователями в XYZ Bank")
  @ValueSource(strings = {
          "Harry Potter",
          "Ron Weasly",
          "Albus Dumbledore",
          "Neville Longbottom"
  })
  @ParameterizedTest()
  @Tag("Auth")
  void checkCustomerLoginTest(String name) {
    open("https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login");
    $(".btn-lg").shouldBe(text("Customer Login")).click();
    $("#userSelect").click();
    $("#userSelect").selectOption(name);
    $("[type='submit']").click();
    $(".fontBig").shouldHave(text(name));

  }


  @CsvSource(value = {
          "standard_user, secret_sauce",
          "problem_user, secret_sauce",
          "performance_glitch_user, secret_sauce"
  })
  @ParameterizedTest()
  @Tag("Auth")
  @DisplayName("Авторизация на сайте www.saucedemo.com под разрешенными пользователями")
  void authorizationUnderDifferentUsersTest(String userName, String userPassword) {
    open("https://www.saucedemo.com/");
    $("#user-name").setValue(userName);
    $("#password").setValue(userPassword);
    $("#login-button").click();

    $("[data-test='title']").shouldBe(text("Products"));
    $(".bm-burger-button").click();
    $("[data-test='logout-sidebar-link']").click();
    $("#login-button").shouldBe(visible);

  }


  static Stream<Arguments> checkTransactionsForDifferentAccounts() {
    return Stream.of(
            Arguments.of("Harry Potter", "1006", "5000"),
            Arguments.of("Ron Weasly", "1008", "10000")
    );
  }

  @MethodSource("checkTransactionsForDifferentAccounts")
  @ParameterizedTest
  @Tag("Func")
  void checkTransactionsForDifferentAccounts(String userName, String account, String amount) {
    open("https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login");
    $(".btn-lg").shouldBe(text("Customer Login")).click();
    $("#userSelect").click();
    $("#userSelect").selectOption(userName);
    $("[type='submit']").click();

    $("#accountSelect").selectOption(account);
    $("[ng-click='deposit()']").click();
    $("[type='number']").setValue(amount);
    $("[type='submit']").click();
    sleep(2000); //либо сайт, либо мой пк, но без слипа не успевает заполниться, хз
    $("[ng-click='transactions()']").click();

    $(".table-bordered > tbody").shouldHave(text(amount));


  }

}
