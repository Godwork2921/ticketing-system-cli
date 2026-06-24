package concurrency;

import com.ticketing.service.ReservationService;
import com.ticketing.dao.SeatDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestDataSeeder;
import util.TestDatabaseCleaner;

import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CacheTest {

    private SeatDAO seatDAO = new SeatDAO();

    @BeforeEach
    void setup() throws SQLException {
        TestDatabaseCleaner.cleanAll();
        TestDataSeeder.seed();
        seatDAO.resetSeat(1L);
    }

    @Test
    void stressTest100Users() throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(100);
        AtomicInteger success = new AtomicInteger();

        ReservationService service = new ReservationService();

        for (int i = 0; i < 100; i++) {

            int user = i;

            executor.submit(() -> {
                try {
                    if (service.reserveSeat(
                            "cache" + user + "@mail.com",
                            1L,
                            1L
                    )) {
                        success.incrementAndGet();
                    }
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(1, success.get());
    }
}