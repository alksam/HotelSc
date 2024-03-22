

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.example.Config.HibernateConfig;
import org.example.DAO.HotelDAO;
import org.example.Entity.Hotel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.codehaus.groovy.vmplugin.v8.IndyGuardsFiltersAndSignatures.isNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class HotelDAOTest {

    private static EntityManagerFactory emf;
    private static HotelDAO hotelDAO;

    @BeforeAll
    static void setUp() {
        emf = HibernateConfig.getEntityManagerFactoryConfig(true);
        hotelDAO = HotelDAO.getInstance(emf);
    }

    @AfterAll
    static void tearDown() {
        emf.close();
    }

    @Test
    @DisplayName("Create new hotel")
    void test1() {
        // Given
        String name = "mohammed";
        String address = "søborg";

        // When
        Hotel hotelCreated = hotelDAO.create(new Hotel("mohammed", "søborg"));

        // Then
        assertEquals(name, hotelCreated.getName());
        assertEquals(address, hotelCreated.getAddress());
    }

    @Test
    @DisplayName("Getting all hotels")
    void test2() {
        // Given
        int expectedHotels = 1;

        // When
        try (EntityManager em = emf.createEntityManager()) {
            Hotel newHotel = new Hotel("Test", "Test");
            em.getTransaction().begin();
            em.persist(newHotel);
            em.getTransaction().commit();
        }
        List<Hotel> hotels = hotelDAO.getAll();

        // Then
        assertTrue(!hotels.isEmpty(), "true");
        assertEquals(expectedHotels, hotels.size());
    }

    @Test
    @DisplayName("Get hotel by id")
    void test3() {
        // Given
        Hotel newHotel = new Hotel("Test", "Test");
        Hotel foundHotel;

        // When
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(newHotel);
            em.getTransaction().commit();

            foundHotel = em.find(Hotel.class, 1);
        }

        // Then
        assertEquals(foundHotel.getName(), newHotel.getName());
        assertEquals(foundHotel.getAddress(), newHotel.getName());
    }

    @Test
    @DisplayName("Update hotel by id")
    void test4() {
        // Given
        String newName = "Youssef";
        String newAddress = "Hillerød";
        int expectedResult = 1;
        Hotel foundHotel;

        try (EntityManager em = emf.createEntityManager()) {
            Hotel newHotel = new Hotel("Test", "Test");
            em.getTransaction().begin();
            em.persist(newHotel);
            em.getTransaction().commit();
            foundHotel = em.find(Hotel.class, 1);
        }

        // When
        foundHotel.setName("Youssef");
        foundHotel.setAddress("Hillerød");
        int result = hotelDAO.update(foundHotel);
        try (EntityManager em = emf.createEntityManager()) {
            foundHotel = em.find(Hotel.class, 1);
        }

        // Then
        assertEquals(result, expectedResult);
        assertEquals(foundHotel.getName(), newName);
        assertEquals(foundHotel.getAddress(), newAddress);
    }

    @Test
    @DisplayName("Delete hotel by id")
    void test5() {
        // Given
        int expectedResult = 1;
        int setHotelId = 1;
        Hotel foundHotel;

        try (EntityManager em = emf.createEntityManager()) {
            Hotel newHotel = new Hotel("Test", "Test");
            em.getTransaction().begin();
            em.persist(newHotel);
            em.getTransaction().commit();
            foundHotel = em.find(Hotel.class, setHotelId);
        }

        // When
        int result = hotelDAO.delete(setHotelId);
        try (EntityManager em = emf.createEntityManager()) {
            foundHotel = em.find(Hotel.class, setHotelId);
        }

        // Then
        assertEquals(result, expectedResult);
        isNull(foundHotel);
    }
}
