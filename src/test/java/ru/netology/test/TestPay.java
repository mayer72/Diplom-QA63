package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.DataHelperSQL;
import ru.netology.page.StartPage;

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
    //Номер карты со статусом DECLINED для оплаты
    void shouldErrorPayWithDECLINEDCard() {

        String status = "DECLINED";
        StartPage page = new StartPage();
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
        //Номер карты со статусом "APPROVED" к оплате"
    void shouldSuccessfulPayWithAPPROVEDCard() {

        String status = "APPROVED";
        StartPage page = new StartPage();
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
    //Номер карты со статусом НЕДЕЙСТВИТЕЛЬНЫЙ для оплаты
    void shouldErrorPayWithINVALIDCard() {

        String status = "INVALID";
        StartPage page = new StartPage();
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
        //Номер кредитной карты с нулевым статусом для зачисления
    void shouldErrorPayWithZEROCard() {

        String status = "ZERO";
        StartPage page = new StartPage();
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
        // ввод пятнадцать цифр в номер карты
    void shouldErrorPayWithFIFTEENCard() {

        String status = "FIFTEEN";
        StartPage page = new StartPage();
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
        // нулевой месяц
    void shouldErrorZeroMonthForPay() {

        String status = "APPROVED";
        StartPage page = new StartPage();
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
        // истёкший срок годности карты месяца
    void shouldErrorOverMonthForPay() {

        String status = "APPROVED";
        StartPage page = new StartPage();
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
        // ввод одной цифры месяца
    void shouldErrorOneDigitMonthForPay() {

        String status = "APPROVED";
        StartPage page = new StartPage();
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
        // ввод нулевого года
    void shouldErrorZeroYearForPay() {

        String status = "APPROVED";
        StartPage page = new StartPage();
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
        // ввод невалидного года из двух цифер
    void shouldErrorMoreYearForPay() {

        String status = "APPROVED";
        StartPage page = new StartPage();
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
        // истекший срок карты по году
    void shouldErrorLessYearForPay() {

        String status = "APPROVED";
        StartPage page = new StartPage();
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
        // название кредита на кириллице
    void shouldErrorCyrillicNameForPayment() {

        String status = "APPROVED";
        StartPage page = new StartPage();
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
        // ввод невалидного имени цифрами
    void shouldErrorNumberNameForPayment() {

        String status = "APPROVED";
        StartPage page = new StartPage();
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
        //ввод имени из одной буквы
    void shouldErrorOneLetterNameForPayment() {

        String status = "APPROVED";
        StartPage page = new StartPage();
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
        // ввод имени специальными символами
    void shouldErrorSpecCharNameForPayment() {

        String status = "APPROVED";
        StartPage page = new StartPage();
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
        // CVC из двух цифер
    void shouldErrorTwoDigCVCForPayment() {

        String status = "APPROVED";
        StartPage page = new StartPage();
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
        // CVC из одной цифры
    void shouldErrorOneDigCVCForPayment() {

        String status = "APPROVED";
        StartPage page = new StartPage();
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
        // незаполненная форма
    void shouldMessageFilInFieldInPay() {

        StartPage page = new StartPage();
        page.buy();
        page.clickContinue();
        page.notificationMessageNumber("Поле обязательно для заполнения");
        page.notificationMessageMonth("Поле обязательно для заполнения");
        page.notificationMessageYear("Поле обязательно для заполнения");
        page.notificationMessageOwner("Поле обязательно для заполнения");
        page.notificationMessageCVC("Поле обязательно для заполнения");
    }
}
