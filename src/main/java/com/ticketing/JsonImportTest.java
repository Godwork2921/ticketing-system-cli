package com.ticketing;

import com.ticketing.model.User;
import com.ticketing.storage.JsonImporter;

import java.util.List;

public class JsonImportTest {

    public static void main(String[] args) {

        JsonImporter importer =
                new JsonImporter();

        List<User> users =
                importer.importUsers();

        users.forEach(System.out::println);
    }
}