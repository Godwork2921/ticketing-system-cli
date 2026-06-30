package service;

import com.ticketing.enums.EventStatus;
import com.ticketing.model.Event;
import com.ticketing.model.Venue;
import com.ticketing.model.Money;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Currency;

public class EventDAOTest {

    public static void main(String[] args) {

        // =====================================================
        // FIXED VENUE (NO SETTERS)
        // =====================================================
        Venue venue = new Venue(
                "Test Venue",
                "Addis Ababa",
                "EAT"
        );

        // =====================================================
        // MONEY VALUE OBJECT (CORRECT)
        // =====================================================
        Money basePrice = new Money(
                BigDecimal.valueOf(100.0)
                        .setScale(2, java.math.RoundingMode.HALF_UP),
                Currency.getInstance("USD")
        );

        // =====================================================
        // EVENT CREATION (UNCHANGED LOGIC)
        // =====================================================
        Event event = new Event(
                1L,
                "Concert",
                venue,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                basePrice,
                EventStatus.ACTIVE,
                new ArrayList<>()
        );

        System.out.println(event);
        System.out.println("Event test passed");
    }
}