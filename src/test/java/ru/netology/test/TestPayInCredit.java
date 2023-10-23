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

public class TestPayInCredit {

    private static DataHelper.CreditRequestEntity credit;
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
    @DisplayName("покупка в кредит с откланённой картой")
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
    @DisplayName("покупка в кредит с одобренной картой")
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
    @DisplayName("ошибка с отклоненной картой")
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
        assertEquals(null, DataHelperSQL.getOrderEntity());
        assertEquals(null, DataHelperSQL.getCreditRequestEntity());

    }

    @Test
    @DisplayName("Номер кредитной карты с нулевым статусом для зачисления")
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
        assertEquals(null, DataHelperSQL.getOrderEntity());
        assertEquals(null, DataHelperSQL.getCreditRequestEntity());

    }

    @Test
    @DisplayName("ввод пятнадцать цифр в номер карты")
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
    @DisplayName("не заполненое поле номера карты")
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
    @DisplayName("нулевой месяц")
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
    @DisplayName("истёкший срок годности карты")
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
    @DisplayName("ввод одной цифры месяца")
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
    @DisplayName("ввод нулевого года")
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
    @DisplayName("ввод невалидного года из двух цифер")
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
    @DisplayName("истекший срок карты по году")
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
    @DisplayName("название кредита на кириллице")
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
    @DisplayName("ввод невалидного имени цифрами")
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
    @DisplayName("ввод имени из одной буквы")
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
    @DisplayName("ввод имени специальными символами")
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
    @DisplayName("CVC из двух цифер")
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
    @DisplayName("CVC из одной цифры")
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
    @DisplayName("должны быть заполнены все поля")
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
