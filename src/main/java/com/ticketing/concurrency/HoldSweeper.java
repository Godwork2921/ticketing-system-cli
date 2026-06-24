package com.ticketing.concurrency;

import com.ticketing.dao.ReservationDAO;
import com.ticketing.dao.SeatDAO;
import com.ticketing.database.DBConnection;
import com.ticketing.enums.SeatStatus;
import com.ticketing.model.Reservation;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HoldSweeper {

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    private final ReservationDAO reservationDAO =
            new ReservationDAO();

    private final SeatDAO seatDAO =
            new SeatDAO();

    public void start() {

        scheduler.scheduleAtFixedRate(() -> {

            try (Connection conn = DBConnection.getConnection()) {

                conn.setAutoCommit(false);

                List<Reservation> expired =
                        reservationDAO.findExpiredHolds(conn);

                for (Reservation r : expired) {

                    seatDAO.updateStatus(
                            conn,
                            r.getSeatId(),
                            SeatStatus.AVAILABLE
                    );

                    reservationDAO.expire(
                            conn,
                            r.getId()
                    );
                }

                conn.commit();

                System.out.println(
                        "[SWEEPER] cleaned: " + expired.size()
                );

            } catch (Exception e) {

                System.err.println(
                        "[SWEEPER ERROR] " + e.getMessage()
                );
            }

        }, 5, 5, TimeUnit.SECONDS);
    }

    public void stop() {

        scheduler.shutdown();

        try {

            if (!scheduler.awaitTermination(
                    3,
                    TimeUnit.SECONDS
            )) {
                scheduler.shutdownNow();
            }

        } catch (Exception e) {

            scheduler.shutdownNow();
        }
    }
}