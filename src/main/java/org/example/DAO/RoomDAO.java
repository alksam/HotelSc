package org.example.DAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import org.example.Entity.Room;

import java.util.List;

public class RoomDAO extends AbstractDAO<Room> {
    private static RoomDAO instance;
    private static EntityManagerFactory emf;

    private RoomDAO(EntityManagerFactory _emf, Class<Room> entityClass) {
        super(_emf, entityClass);
    }

    public static RoomDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new RoomDAO(emf, Room.class);
        }
        return instance;
    }

    public List<Room> getAllRoomsByHotelId(int hotelId) {
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("SELECT r FROM Room r WHERE r.hotel.id = :hotelId");
            query.setParameter("hotelId", hotelId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Room> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("SELECT r FROM Room r");
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}