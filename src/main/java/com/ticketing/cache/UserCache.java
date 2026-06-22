package com.ticketing.cache;

import com.ticketing.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserCache {

    private static final Map<String, User> CACHE =
            new HashMap<>();

    private UserCache() {}

    public static User get(String email) {
        return CACHE.get(email);
    }

    public static void put(User user) {

        if (user != null) {
            CACHE.put(user.getEmail(), user);
        }
    }

    public static void remove(String email) {
        CACHE.remove(email);
    }

    public static void clear() {
        CACHE.clear();
    }

    public static boolean contains(String email) {
        return CACHE.containsKey(email);
    }
}