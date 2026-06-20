package com.ticketing;

import com.ticketing.ui.menu.LoginMenu;
import com.ticketing.util.DataLoader;

public class Main {

    public static void main(String[] args) {

        // Load initial demo users (optional for testing)
        DataLoader.loadSampleUsers();

        // Start login system
        new LoginMenu().start();
    }
}