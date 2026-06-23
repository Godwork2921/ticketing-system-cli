package concurrency;
import com.ticketing.service.ReservationService;

import java.util.concurrent.*;
public class SeatRaceTest {


           public static void main(String[] args) throws Exception {

            ExecutorService executor = Executors.newFixedThreadPool(10);

            CountDownLatch latch = new CountDownLatch(10);

            for (int i = 0; i < 10; i++) {

                int user = i;

                executor.submit(() -> {
                    try {
                        ReservationService service = new ReservationService();
                        service.reserveSeat(
                                "user" + user + "@mail.com",
                                1L,
                                1L
                        );
                    } catch (Exception e) {
                        System.out.println("FAILED: " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();

            System.out.println("TEST COMPLETED");
        }
    }

