package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.DataHelperSQL;
import ru.netology.page.StartOfCardFunctionality;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelperSQL.cleanDatabase;

public class TestPay {

    private static DataHelper.PaymentEntity payment;
    private static DataHelper.OrderEntity order;

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
    @DisplayName("Номер карты со статусом DECLINED для оплаты")
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
    @DisplayName("Номер карты со статусом \"APPROVED\" к оплате")
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
    @DisplayName("Номер карты со статусом НЕДЕЙСТВИТЕЛЬНЫЙ для оплаты")
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

        assertEquals(null, DataHelperSQL.getOrderEntity());
        assertEquals(null, DataHelperSQL.getPaymentEntity());
    }

    @Test
    @DisplayName("Номер кредитной карты с нулевым статусом для зачисления")
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

        assertEquals(null, DataHelperSQL.getOrderEntity());
        assertEquals(null, DataHelperSQL.getPaymentEntity());
    }

    @Test
    @DisplayName("С не заполненым полем номера карты")
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
    @DisplayName("Bвод пятнадцать цифр в номер карты")
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
    @DisplayName("нулевой месяц")
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
    @DisplayName("Истёкший срок годности карты месяца")
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
    @DisplayName("Ввод одной цифры месяца")
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
    @DisplayName("Ввод нулевого года")
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
    @DisplayName("Ввод невалидного года из двух цифер")
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
    @DisplayName("Истекший срок карты по году")
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
    @DisplayName("Название кредита на кириллице")
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
    @DisplayName("Ввод невалидного имени цифрами")
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
    @DisplayName("Ввод имени из одной буквы")
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
    @DisplayName("Ввод имени специальными символами")
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
    @DisplayName("CVC из двух цифер")
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
    @DisplayName("CVC из одной цифры")
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
    @DisplayName("Незаполненная форма")
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
