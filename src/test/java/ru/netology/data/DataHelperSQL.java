package ru.netology.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.Connection;
import java.sql.DriverManager;


public class DataHelperSQL {
    private static QueryRunner runner = new QueryRunner();

    public DataHelperSQL() {
    }

    private static String url = System.getProperty("db.url");
    private static String user = System.getProperty("db.user");
    private static String password = System.getProperty("db.password");


    @SneakyThrows
    private static Connection getConn() {
        return DriverManager.getConnection(url, user, password);

    }

    @SneakyThrows
    public static void cleanDatabase() {
        Connection conn = getConn();
        runner.execute(conn, "DELETE FROM payment_entity");
        runner.execute(conn, "DELETE FROM credit_request_entity");
        runner.execute(conn, "DELETE FROM order_entity");
    }

    @SneakyThrows
    public static DataHelper.PaymentEntity getPaymentEntity() {
        String codeSQL = "SELECT * FROM payment_entity ORDER BY created DESC LIMIT 1";
        Connection conn = getConn();
        return runner.query(conn, codeSQL, new BeanHandler<>(DataHelper.PaymentEntity.class));
    }

    @SneakyThrows
    public static DataHelper.CreditRequestEntity getCreditRequestEntity() {
        String codeSQL = "SELECT * FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        Connection conn = getConn();
        return runner.query(conn, codeSQL, new BeanHandler<>(DataHelper.CreditRequestEntity.class));
    }

    @SneakyThrows
    public static DataHelper.OrderEntity getOrderEntity() {
        String codeSQL = "SELECT * FROM order_entity ORDER BY created DESC LIMIT 1";
        Connection conn = getConn();
        return runner.query(conn, codeSQL, new BeanHandler<>(DataHelper.OrderEntity.class));
    }


}