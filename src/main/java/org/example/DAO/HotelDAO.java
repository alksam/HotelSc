package org.example.DAO;

import io.javalin.http.Context;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.example.Entity.Hotel;
import org.example.Entity.Room;
import org.example.Entity.User;
import org.example.dtos.RoomDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class HotelDAO extends AbstractDAO<Hotel> {

    private static HotelDAO instance;
    private static EntityManagerFactory emf;

    private HotelDAO(EntityManagerFactory _emf, Class<Hotel> entityClass) {
        super(_emf, entityClass);
    }

    public static HotelDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new HotelDAO(emf, Hotel.class);
        }
        return instance;
    }

    public List<Hotel> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Hotel> query = em.createQuery("SELECT h FROM Hotel h", Hotel.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public int delete(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Hotel hotelFound = em.find(Hotel.class, id);

            if (hotelFound != null) {
                // Remove all associated rooms first
                for (Room room : hotelFound.getRooms()) {
                    em.remove(room);
                }

                // Remove the hotel itself
                em.remove(hotelFound);

                em.getTransaction().commit();
                return 1;
            }
            return 0;
        } finally {
            em.close();
        }
    }
}