package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.example.Config.HibernateConfig;
import org.example.Controller.HotelController;
import org.example.DAO.HotelDAO;
import org.example.DAO.UserDAO;
import org.example.Entity.Hotel;
import org.example.Entity.Role;
import org.example.Entity.Room;
import org.example.Entity.User;
import org.example.dtos.RoomDTO;
import org.example.exceptions.EntityNotFoundException;



public class Main2 {

  public static void main(String[] args)

  {

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryConfig(false);
        EntityManager em = emf.createEntityManager();
        HotelDAO hotelDAO = HotelDAO.getInstance(emf);
        UserDAO userDAO = UserDAO.getInstance(emf);
        RoomDTO roomDTO = new RoomDTO();
        HotelController hotelController = new HotelController(emf);

        em.getTransaction().begin();




        User user1 = new User("Amjad ", "3212");
        User user = new User("eee", "1234");
        Role adminRole = new Role("admin");

        Hotel hotel1 = new Hotel("Hotel A", "Roskilde vej", Hotel.HotelType.BUDGET );
        Hotel hotel2 = new Hotel("Hotel B", "Roskilde vej" , Hotel.HotelType.LUXURY);




        hotel1.addRoom(new Room(1, 500, Room.RoomType.SINGLE));
        hotel1.addRoom(new Room(2, 800, Room.RoomType.DOUBLE));

        hotel2.addRoom(new Room(1, 500, Room.RoomType.SINGLE));
        hotel2.addRoom(new Room(2, 800, Room.RoomType.DOUBLE));

       // hotelController.getAll(H);
        user.addHotel(hotel1);
        user1.addHotel(hotel2);

        /*try {
            User verifiedUser = userDAO.verifyUser("am", "1234");
            System.out.println(user.getUsername());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }**/

        //hotelDAO.getById(1);


            hotelDAO.create(hotel1);
            hotelDAO.create(hotel2);
            em.persist(user);
            em.persist(user1);

            em.persist(adminRole);


            em.getTransaction().commit();
            em.close();
        }

    }
