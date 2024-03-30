import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManagerFactory;
import org.eclipse.jetty.http.HttpStatus;
import org.example.Config.ApplicationConfig;
import org.example.Config.HibernateConfig;
import org.example.Controller.HotelController;
import org.example.DAO.UserDAO;
import org.example.Entity.Hotel;
import org.example.Entity.Role;
import org.example.Entity.Room;
import org.example.Entity.User;
import org.example.Route.App;
import org.example.dtos.HotelDTO;
import org.example.dtos.RoomDTO;
import org.example.dtos.TokenDTO;
import org.example.dtos.UserDTO;
import org.example.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SETUPtest {


    private static final String BASE_URL = "http://localhost:7040/api";
    private static HotelController hotelController;
    private static EntityManagerFactory emf;
    private static ApplicationConfig app;
    private static Integer port = 7040;

    private static String userToken;
    private static String userUsername;
    private static String adminToken;
    private static String adminUsername;

    @BeforeAll
    static void beforeAll() {
        emf = HibernateConfig.getEntityManagerFactoryConfig(false);

        app = ApplicationConfig.getInstance();
        app.initiateServer()
                .startServer(port)
                .setExceptionHandlers()
                .checkSecurityRoles()
                .setRoute(App.addRoutes());

    }

    @BeforeEach
    void setUp() {


        Hotel hotel1 = new Hotel("Hotel A", "Lyngby vej", Hotel.HotelType.BUDGET);
        Hotel hotel2 = new Hotel("Hotel B", "Roskilde vej", Hotel.HotelType.LUXURY);

        hotel1.addRoom(new Room(1, 500, Room.RoomType.SINGLE));
        hotel1.addRoom(new Room(2, 800, Room.RoomType.DOUBLE));

        hotel2.addRoom(new Room(1, 500, Room.RoomType.SINGLE));
        hotel2.addRoom(new Room(2, 800, Room.RoomType.DOUBLE));
        User user = new User("username", "password");
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN"); // Simuler brugerroller fra token
        UserDTO userDTO = new UserDTO("username", roles);
        TokenDTO tokenDTO = new TokenDTO("valid_token", "username");
        try (var em = emf.createEntityManager()) {

            em.getTransaction().begin();
            em.persist(hotel1);
            em.persist(hotel2);
            em.persist(user);


            em.getTransaction().commit();
        }

        // Initialisering af tokens og brugernavne
        userToken = tokenDTO.getToken();
        userUsername = userDTO.getUsername();
        adminToken = tokenDTO.getToken(); // Antages at admin-token er det samme som bruger-token
        adminUsername = userDTO.getUsername(); // Antages at admin-brugernavnet er det samme som bruger-brugernavnet

    }




    @AfterAll
    static void tearDown() {
        emf.close();
        app.stopServer();
    }





    @Test
    @DisplayName("Test server is running.")
    public void testServerRunning() {
        RestAssured
                .given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/hotels")
                .then()
                .statusCode(200)
                .body("size()", is(2));
    }

    @Test
    @DisplayName("Testing register user and token retrieval")
    void getUserRoutes() {



        String setBody = "{\"username\": \"UserTest2\", \"password\": \"1234\"}";

        String response =
                RestAssured
                        .given()
                        .contentType(ContentType.JSON)
                        .body(setBody)
                        .when()
                        .post(BASE_URL + "/auth/register")  // Tilføj '/api/v1' her
                        .then()
                        .statusCode(201)
                        .extract().asString();


        String token = JsonPath.from(response).getString("token");
        String username = JsonPath.from(response).getString("username");
        String role = JsonPath.from(response).getString("role");
        System.out.println("Response: " + response + "\nToken: " + token + "\nUsername: " + username);
    }
    @Test
    @DisplayName("Login and retrieve token")
    void test0() {

        String setRegisterBody = "{\"username\": \"UserTest3\", \"password\": \"1234\"}";
        RestAssured
                .given()
                .contentType(io.restassured.http.ContentType.JSON)
                .body(setRegisterBody)
                .when()
                .post("/auth/register")
                .then();

        String setLoginBody = "{\"username\": \"UserTest3\", \"password\": \"1234\"}";

        String response = RestAssured
                .given()
                .contentType(io.restassured.http.ContentType.JSON)
                .body(setLoginBody)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract().asString();

        String token = JsonPath.from(response).getString("token");
        String username = JsonPath.from(response).getString("username");
        System.out.println("Response: " + response + "\nToken: " + token + "\nUsername: " + username);
    }

    @Test
    void readAll() {
        // Given -> When -> Then
        List<HotelDTO> hotelDtoList =
                given()
                        .contentType(ContentType.JSON)
                        .when()
                        .get(BASE_URL + "/hotels")  // Tilføj '/api/v1' her
                        .then()
                        .assertThat()
                        .statusCode(200)  // could also just be 200
                        .extract().body().jsonPath().getList("", HotelDTO.class);


        HotelDTO h2DTO = new HotelDTO();

        assertEquals(hotelDtoList.size(), 2);
        assertThat(hotelDtoList, containsInAnyOrder( h2DTO));
    }

    @Test
    void create() {
        try (var em = emf.createEntityManager()) {
            // Database operations


            Hotel h3 = new Hotel("Cab-inn", "Østergade 2", Hotel.HotelType.BUDGET);
            Room r1 = new Room(117, 4500, Room.RoomType.SINGLE);
            Room r2 = new Room(118, 2300, Room.RoomType.DOUBLE);
            h3.addRoom(r1);
            h3.addRoom(r2);
            HotelDTO newHotel = new HotelDTO();


            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(newHotel, HotelDTO.class);
            System.out.println(json);
            //  List<RoomDTO> roomDtos =

            given()
                    .contentType(ContentType.JSON)
                    .body(json)
                    .when()
                    .post(BASE_URL + "/hotels")
                    .then()
                    .statusCode(201);
        }  }
}
