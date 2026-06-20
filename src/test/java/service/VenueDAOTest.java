package service;

import com.ticketing.dao.VenueDAO;
import com.ticketing.model.Venue;

public class VenueDAOTest {

    public static void main(String[] args) {

        VenueDAO dao = new VenueDAO();

        dao.save(
                new Venue(
                        1L,
                        "Millennium Hall",
                        "Addis Ababa",
                        "Africa/Addis_Ababa"
                )
        );

        dao.findAll()
                .forEach(System.out::println);
    }
}