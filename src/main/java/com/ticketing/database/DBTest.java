package com.ticketing.database;

public class DBTest {

    public static void main(String[] args) {

        try {

            var connection =
                    DBConnection.getConnection();

            System.out.println(
                    "Database connected successfully!"
            );

            connection.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}