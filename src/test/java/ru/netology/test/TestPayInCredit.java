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

public class TestPayInCredit {

    private static Models.CreditRequestEntity credit;
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
    @DisplayName("Purchase on credit with a rejected card")
    void PurchaseOnCreditWithARejectedCard() {

        String status = "DECLINED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationError();

        assertEquals(status, DataHelperSQL.getCreditRequestEntity().getStatus());

    }

    @Test
    @DisplayName("Purchase on credit with an approved card")
    void purchaseOnCreditWithAnApprovedCard() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationOk();

        credit = DataHelperSQL.getCreditRequestEntity();
        order = DataHelperSQL.getOrderEntity();
        assertEquals(status, credit.getStatus());
        assertEquals(credit.getBank_id(), order.getPayment_id());
        assertEquals(credit.getId(), order.getCredit_id());

    }

    @Test
    @DisplayName("Error with rejected card")
    void errorWithRejectedCard() {

        String status = "INVALID";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationError();

        assertNull(DataHelperSQL.getOrderEntity(), "Не действующая карта");
        assertNull(DataHelperSQL.getCreditRequestEntity());

    }

    @Test
    @DisplayName("Credit card number with zero status for enrollment")
    void shouldErrorCreditWithZEROCard() {

        String status = "ZERO";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationError();

        assertNull(DataHelperSQL.getOrderEntity(), "Номер карты не должен быть с нулевыми значениями");
        assertNull(DataHelperSQL.getCreditRequestEntity());

    }

    @Test
    @DisplayName("Enter fifteen digits into the card number")
    void shouldErrorCreditWithFIFTEENCard() {

        String status = "FIFTEEN";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationMessageNumber("Неверный формат");

    }

    @Test
    @DisplayName("Empty card number field")
    void shouldErrorCreditWithEMPTYCard() {

        String status = "EMPTY";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.notificationMessageNumber("Неверный формат");

    }

    @Test
    @DisplayName("Zero month")
    void shouldErrorZeroMonthForCredit() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buyInCredit();
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
    void shouldErrorOverMonthForCredit() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buyInCredit();
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
    void shouldErrorOneDigitMonthForCredit() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buyInCredit();
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
    void shouldErrorZeroYearForCredit() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buyInCredit();
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
    void shouldErrorMoreYearForCredit() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buyInCredit();
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
    void shouldErrorLessYearForCredit() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buyInCredit();
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
    void shouldErrorCyrillicNameForCredit() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buyInCredit();
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
    void shouldErrorNumberNameForCredit() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buyInCredit();
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
    void shouldErrorOneLetterNameForCredit() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buyInCredit();
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
    void shouldErrorSpecCharNameForCredit() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buyInCredit();
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
    void shouldErrorTwoDigCVCForCredit() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buyInCredit();
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
    void shouldErrorOneDigCVCForCredit() {

        String status = "APPROVED";
        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buyInCredit();
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
    void AllFieldsMustBeFilledIn() {

        StartOfCardFunctionality page = new StartOfCardFunctionality();
        page.buyInCredit();
        page.clickContinue();
        page.notificationMessageNumber("Поле обязательно для заполнения");
        page.notificationMessageMonth("Поле обязательно для заполнения");
        page.notificationMessageYear("Поле обязательно для заполнения");
        page.notificationMessageOwner("Поле обязательно для заполнения");
        page.notificationMessageCVC("Поле обязательно для заполнения");
    }
}
