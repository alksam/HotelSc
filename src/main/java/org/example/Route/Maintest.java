package org.example.Route;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.example.Config.HibernateConfig;
import org.example.Controller.HotelController;
import org.example.DAO.HotelDAO;
import org.example.DAO.UserDAO;
import org.example.Entity.Hotel;
import org.example.Entity.Role;
import org.example.Entity.Room;
import org.example.Entity.User;
import org.example.dtos.HotelDTO;
import org.example.dtos.RoomDTO;
import org.example.dtos.TokenDTO;
import org.example.dtos.UserDTO;

import java.util.HashSet;
import java.util.Set;

public class Maintest {

    public static void main(String[] args){
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryConfig(false);
        EntityManager em = emf.createEntityManager();
        HotelDAO hotelDAO = HotelDAO.getInstance(emf);
        UserDAO userDAO = UserDAO.getInstance(emf);
        HotelController hotelController = new HotelController(emf);





        Hotel h3 = new Hotel("Cab-inn", "Østergade 2", Hotel.HotelType.BUDGET);
    Room r1 = new Room(117, 4500, Room.RoomType.SINGLE);
    Room r2 = new Room(118, 2300, Room.RoomType.DOUBLE);
        h3.addRoom(r1);
        h3.addRoom(r2);
    HotelDTO newHotel = new HotelDTO();
        User user = new User("username", "password");
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN"); // Simuler brugerroller fra token
        UserDTO userDTO = new UserDTO("username", roles);
        TokenDTO tokenDTO = new TokenDTO("valid_token", "username");




    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String json = gson.toJson(newHotel, HotelDTO.class);
    String json2 = gson.toJson(userDTO, UserDTO.class);
    String json3 = gson.toJson(tokenDTO, TokenDTO.class);
    String json4 = gson.toJson(user, User.class);
        System.out.println(json);
        System.out.println(json2);
        System.out.println(json3);
        System.out.println(json4);



        hotelDAO.create(h3);

        hotelDAO.getAll().forEach(System.out::println);
        hotelDAO.getById(1).getRoomasStrings().forEach(System.out::println);
        user.addHotel(h3);
//        userDAO.createRole();
//        try {
//            User verifiedUser = userDAO.verif yUser("am", "1234");
//            System.out.println(user.getUsername());
//        } catch (EntityNotFoundException e) {
//            e.printStackTrace();
//        }


}
 }
