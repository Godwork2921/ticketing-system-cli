package com.ticketing.util;

import com.ticketing.database.DBConnection;

import java.sql.Connection;
import java.sql.Statement;

public class TestDatabaseCleaner {

    public static void cleanAll() {

        String[] sqls = {
                "DELETE FROM reservations",
                "DELETE FROM seats",
                "DELETE FROM events",
                "DELETE FROM venues"
        };

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {

            for (String sql : sqls) {
                st.executeUpdate(sql);
            }

            // IMPORTANT: reset sequences if using SERIAL/BIGSERIAL
            st.executeUpdate("ALTER SEQUENCE reservations_id_seq RESTART WITH 1");

        } catch (Exception e) {
            throw new RuntimeException("DB cleanup failed", e);
        }
    }
}