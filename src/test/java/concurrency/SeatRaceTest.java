package concurrency;

import com.ticketing.dao.SeatDAO;
import com.ticketing.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestDataSeeder;
import util.TestDatabaseCleaner;

import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeatRaceTest {

    private final SeatDAO seatDAO = new SeatDAO();

    @BeforeEach
    void setup() throws SQLException {

        TestDatabaseCleaner.cleanAll();
        TestDataSeeder.seed();
        seatDAO.resetSeat(1L);
    }

    @Test
    void onlyOneReservationShouldSucceed() throws Exception {

        final int THREADS = 10;

        ExecutorService executor =
                Executors.newFixedThreadPool(THREADS);

        CyclicBarrier barrier =
                new CyclicBarrier(THREADS);

        CountDownLatch finished =
                new CountDownLatch(THREADS);

        AtomicInteger success =
                new AtomicInteger();

        ReservationService service =
                new ReservationService();

        for (int i = 0; i < THREADS; i++) {

            final int user = i;

            executor.submit(() -> {

                try {

                    // Wait until EVERY thread is ready
                    barrier.await();

                    if (service.reserveSeat(
                            "user" + user + "@mail.com",
                            1L,
                            1L
                    )) {

                        success.incrementAndGet();
                    }

                } catch (Exception ignored) {

                } finally {

                    finished.countDown();
                }
            });
        }

        finished.await();

        executor.shutdown();

        assertEquals(
                1,
                success.get(),
                "Only one reservation should succeed"
        );
    }
}