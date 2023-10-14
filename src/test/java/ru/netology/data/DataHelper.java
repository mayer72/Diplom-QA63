package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

public class DataHelper {
    private DataHelper() {
    }

    private static Faker faker = new Faker(new Locale("en"));
    private static Faker fakerRu = new Faker(new Locale("ru"));



    public static String getCardNumberByStatus(String status) {
        if (status.equalsIgnoreCase("APPROVED")) {
            return "4444 4444 4444 4441";
        } else if (status.equalsIgnoreCase("DECLINED")) {
            return "4444 4444 4444 4442";
        } else if (status.equalsIgnoreCase("INVALID")) {
            return "4444 4444 4444 4443";
        } else if (status.equalsIgnoreCase("ZERO")) {
            return "0000 0000 0000 0000";
        } else if (status.equalsIgnoreCase("FIFTEEN")) {
            return faker.numerify("#### #### #### ###");
        }
        return null;
    }


    public static String generateMonthPlus(int shift) {
        return LocalDate.now().plusMonths(shift).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String getZero() {
        return "00";
    }

    public static String getMonthOver() {
        return "13";
    }

    public static String getMonthOneDig() {
        return faker.numerify("#");
    }

    public static String generateYearPlus(int shift) {
        return LocalDate.now().plusYears(shift).format(DateTimeFormatter.ofPattern("yy"));
    }


    public static String generateYearMinus(int shift) {
        return LocalDate.now().minusYears(shift).format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String generateHolder() {

        return faker.name().firstName().toUpperCase(Locale.ROOT) + " "
                + faker.name().lastName().toUpperCase(Locale.ROOT);
    }

    public static String generateHolderCyrillic() {

        return fakerRu.name().firstName().toUpperCase(Locale.ROOT) + " "
                + fakerRu.name().lastName().toUpperCase(Locale.ROOT);
    }

    public static String generateHolderNumeric() {
        return faker.numerify("########");
    }

    public static String generateHolderOneSymbol() {
        return faker.letterify("?").toUpperCase(Locale.ROOT);
    }

    public static String generateHolderSpecChar(int streamSize) {
        String special = "!@#$^&*";
        return new Random().ints(streamSize, 0, special.length())
                .mapToObj(special::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    public static String generateCVC(int amount) {
        String str = "###";
        return faker.numerify(str.substring(0,amount));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentEntity {
        private String id;
        private int amount;
        private Timestamp created;
        private String status;
        private String transaction_id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditRequestEntity {
        private String id;
        private String bank_id;
        private Timestamp created;
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderEntity {
        private String id;
        private Timestamp created;
        private String credit_id;
        private String payment_id;
    }
    
}
