package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.DataHelperSQL;
import ru.netology.data.Models;
import ru.netology.page.StartOfCardFunctionality;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.data.DataHelperSQL.cleanDatabase;

public class TestPay {

    private static Models.PaymentEntity payment;
    private static Models.OrderEntity order;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:8080");
    }

    @AfterEach
    public void cleanData() {
        cleanDatabase();
    }

    @Test
    @DisplayName("Card number with DECLINED status for payment")
    void shouldErrorPayWithDECLINEDCard() {

        String status = "DECLINED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationError();
        assertEquals(status, DataHelperSQL.getPaymentEntity().getStatus());
    }

    @Test
    @DisplayName("Card number with the status \"APPROVED\" for payment")
    void shouldSuccessfulPayWithAPPROVEDCard() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        int price = page.getPriceInKops();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationOk();

        payment = DataHelperSQL.getPaymentEntity();
        order = DataHelperSQL.getOrderEntity();
        assertEquals(status, payment.getStatus());
        assertEquals(price, payment.getAmount());
        assertEquals(payment.getTransaction_id(), order.getPayment_id());
    }

    @Test
    @DisplayName("Card number with status INVALID for payment")
    void shouldErrorPayWithINVALIDCard() {

        String status = "INVALID";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationError();

        assertNull(DataHelperSQL.getOrderEntity(), "Не действующая карта");
        assertNull(DataHelperSQL.getPaymentEntity());
    }

    @Test
    @DisplayName("Credit card number with zero status for enrollment")
    void shouldErrorPayWithZEROCard() {

        String status = "ZERO";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationError();

        assertNull(DataHelperSQL.getOrderEntity(), "Номер карты не должен быть с нулевыми значениями");
        assertNull(DataHelperSQL.getPaymentEntity());
    }

    @Test
    @DisplayName("With the card number field blank")
    void shouldErrorPayWithEMPTYCard() {

        String status = "EMPTY";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationMessageNumber("Неверный формат");
    }

    @Test
    @DisplayName("Enter fifteen digits into the card number")
    void shouldErrorPayWithFIFTEENCard() {

        String status = "FIFTEEN";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationMessageNumber("Неверный формат");
    }

    @Test
    @DisplayName("zero month")
    void shouldErrorZeroMonthForPay() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.getZero());
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationMessageMonth("Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("Expired month card")
    void shouldErrorOverMonthForPay() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.getMonthOver());
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationMessageMonth("Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("Entering one digit of the month")
    void shouldErrorOneDigitMonthForPay() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.getMonthOneDig());
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationMessageMonth("Неверный формат");
    }

    @Test
    @DisplayName("Entering a zero year")
    void shouldErrorZeroYearForPay() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.getZero());
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationMessageYear("Истёк срок действия карты");
    }

    @Test
    @DisplayName("Entering an invalid year from two digits")
    void shouldErrorMoreYearForPay() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(10));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationMessageYear("Неверно указан срок действия карты");
    }

    @Test
    @DisplayName("Expired card term by year")
    void shouldErrorLessYearForPay() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearMinus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationMessageYear("Истёк срок действия карты");
    }

    @Test
    @DisplayName("Loan name in Cyrillic")
    void shouldErrorCyrillicNameForPayment() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderCyrillic());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationMessageOwner("Неверный формат");
    }

    @Test
    @DisplayName("Entering an invalid name in numbers")
    void shouldErrorNumberNameForPayment() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderNumeric());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationMessageOwner("Неверный формат");
    }

    @Test
    @DisplayName("Entering a one-letter name")
    void shouldErrorOneLetterNameForPayment() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderOneSymbol());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationMessageOwner("Неверный формат");
    }

    @Test
    @DisplayName("Entering a name using special characters")
    void shouldErrorSpecCharNameForPayment() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderSpecChar(5));
        page.inputCVC(3);
        page.clickContinue();
        page.notificationMessageOwner("Неверный формат");
    }

    @Test
    @DisplayName("CVC of two digits")
    void shouldErrorTwoDigCVCForPayment() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(2);
        page.clickContinue();
        page.notificationMessageCVC("Неверный формат");
    }

    @Test
    @DisplayName("Single digit CVC")
    void shouldErrorOneDigCVCForPayment() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(1);
        page.clickContinue();
        page.notificationMessageCVC("Неверный формат");
    }

    @Test
    @DisplayName("Incomplete form")
    void shouldMessageFilInFieldInPay() {

        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buy();
        page.clickContinue();
        page.notificationMessageNumber("Поле обязательно для заполнения");
        page.notificationMessageMonth("Поле обязательно для заполнения");
        page.notificationMessageYear("Поле обязательно для заполнения");
        page.notificationMessageOwner("Поле обязательно для заполнения");
        page.notificationMessageCVC("Поле обязательно для заполнения");
    }
}
