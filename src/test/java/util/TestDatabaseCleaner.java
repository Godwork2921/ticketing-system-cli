package util;

import com.ticketing.database.DBConnection;

import java.sql.Connection;
import java.sql.Statement;

public class TestDatabaseCleaner {

    public static void cleanAll() {

//        if (!"test".equals(System.getProperty("env"))) {
//            throw new RuntimeException("DB cleanup allowed only in TEST environment");
//        }

        String[] sqls = {
                "DELETE FROM reservations",
                "DELETE FROM seats",
                "DELETE FROM events",
                "DELETE FROM venues",
                "DELETE FROM users"
        };

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {

            conn.setAutoCommit(false);

            for (String sql : sqls) {
                st.executeUpdate(sql);
            }

            conn.commit();

        } catch (Exception e) {
            throw new RuntimeException("DB cleanup failed", e);
        }
    }
}