
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.example.Config.ApplicationConfig;
import org.example.Config.HibernateConfig;
import org.example.DAO.UserDAO;
import org.example.Entity.Hotel;
import org.example.Entity.Role;
import org.example.Entity.Room;
import org.example.Route.App;
import org.example.Route.Route;
import org.example.dtos.HotelDTO;
import org.example.dtos.RoomDTO;
import org.junit.jupiter.api.*;
import org.testcontainers.shaded.com.google.common.annotations.VisibleForTesting;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;




class AppRestTest {

    private static EntityManagerFactory emf;
    private static ApplicationConfig app;
    private static Integer port = 7007;
    private static String userToken;
    private static String userUsername;
    private static String adminToken;
    private static String adminUsername;


    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost:7007/api";
        emf = HibernateConfig.getEntityManagerFactoryConfig(false);


        app = ApplicationConfig.getInstance();
        app.initiateServer()
                .startServer(port)
                .setExceptionHandlers()
                .checkSecurityRoles()
                .setRoute(App.addRoutes());

    }

    @AfterAll
    static void tearDown() {
        emf.close();
        app.stopServer();
    }

    @BeforeEach
    void beforeEach(){

        //Lav objekter

        Hotel hotel1 = new Hotel("Hotel A", "Lyngby vej",   Hotel.HotelType.BUDGET);
        Hotel hotel2 = new Hotel("Hotel B", "Roskilde vej", Hotel.HotelType.LUXURY);

        hotel1.addRoom(new Room(1, 500, Room.RoomType.SINGLE));
        hotel1.addRoom(new Room(2, 800, Room.RoomType.DOUBLE));

        hotel2.addRoom(new Room(1, 500, Room.RoomType.SINGLE));
        hotel2.addRoom(new Room(2, 800, Room.RoomType.DOUBLE));

        UserDAO userDAO = UserDAO.getInstance(emf);
        Role role = new Role("admin");
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(hotel1);
            em.persist(hotel2);
            em.persist(role);
            em.getTransaction().commit();
        }

        // Creating a User.
        String requestBody = "{\"username\": \"UserTest\", \"password\": \"1234\"}";

        String response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(201) // Expecting a 201 Created status
                .extract().asString(); // Extracting the response body as a string

        // Extract using JsonPath
        userToken = JsonPath.from(response).getString("token");
        userUsername = JsonPath.from(response).getString("username");

        String requestBodyAdmin = "{\"username\": \"AdminTest\", \"password\": \"1234\"}";

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(requestBodyAdmin)
                .when()
                .post("/auth/register");

        userDAO.addUserRole("AdminTest", "admin");

        String loginDetails = "{\"username\": \"AdminTest\", \"password\": \"1234\"}";
        String login = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(loginDetails)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200) // Expecting a 201 Created status
                .extract().asString(); // Extracting the response body as a string

        // Extract using JsonPath
        adminToken = JsonPath.from(login).getString("token");
        adminUsername = JsonPath.from(login).getString("username");
    }

    @AfterEach
    void AfterEach(){

        //Tøm databasen

        //fx
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Room").executeUpdate();
            em.createQuery("DELETE FROM Hotel").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.getTransaction().commit();
        }

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
                        .post("/auth/register")
                        .then()
                        .statusCode(201)
                        .extract().asString();


        String token = JsonPath.from(response).getString("token");
        String username = JsonPath.from(response).getString("username");
        System.out.println("Response: "+response + "\nToken: " + token +"\nUsername: "+username);
    }

    @Test
    @DisplayName("Login and retrieve token")
    void test0() {

        String setRegisterBody = "{\"username\": \"UserTest3\", \"password\": \"1234\"}";
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(setRegisterBody)
                .when()
                .post("/auth/register")
                .then();

        String setLoginBody = "{\"username\": \"UserTest3\", \"password\": \"1234\"}";

        String response = RestAssured
                .given()
                .contentType(ContentType.JSON)
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
    @DisplayName("Posting a hotel to the database.")
    public void test2() {

        Hotel postHotel = new Hotel("TestName", "TestAddress", Hotel.HotelType.BUDGET);

        RestAssured
                .given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(postHotel)
                .when()
                .post("/hotels")
                .then()
                .statusCode(200)
                .body("name", equalTo("TestName"))
                .body("address", equalTo("TestAddress"));
    }

    @Test
    @DisplayName("Reading hotel with specific id 1.")
    public void test3() {
        int id = 1;

        RestAssured
                .given()
                .header("Authorization", "Bearer " + adminToken) // Concatenate "Bearer " with userToken
                .contentType(ContentType.JSON)
                .pathParam("id", id) // Passing id as a path parameter
                .when()
                .get("/hotels/{id}") // Using GET request
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("Hotel A"))
                .body("address", equalTo("Lyngby vej"));
    }

    @Test
    @DisplayName("Reading hotel's room with specific hotel id 1.")
    public void test4() {

        int setId = 1;
        int expectedRoomSize = 2;
        int expectedStatusCode = 200;

        RestAssured
                .given()
                .header("Authorization", "Bearer " + adminToken) // Concatenate "Bearer " with userToken
                .contentType(ContentType.JSON)
                .pathParam("id", setId)
                .when()
                .get("/hotels/{id}/rooms")
                .then().log().all()
                .statusCode(expectedStatusCode)
                .body("rooms", hasSize(expectedRoomSize));
    }


    @Test
    @DisplayName("Returning the list as an HotelDTOs.")
    public void test5() {

        String[] expectedNames = {"Hotel A", "Hotel B"};
        String[] expectedAddresses = {"Lyngby vej", "Roskilde vej"};

        int expectedSize = 2;

        List<HotelDTO> list = RestAssured
                .given()
                .header("Authorization", "Bearer " + adminToken) // Concatenate "Bearer " with userToken
                .when()
                .get("/hotels")
                .then()
                .extract()
                .body()
                .jsonPath().getList("", HotelDTO.class);

        assertEquals(expectedSize, list.size());

        for (int i = 0; i < expectedSize; i++) {
            assertThat(list.get(i).getName(), equalTo(expectedNames[i]));
            assertThat(list.get(i).getAddress(), equalTo(expectedAddresses[i]));
        }
    }

    @Test
    @DisplayName("Updating an existing hotel.")
    public void test6() {

        int hotelId = 1;
        String newName = "Hotel C";
        String newAddress = "Risvej";
        int expectedStatusCode = 200;

        HotelDTO updatedHotel = new HotelDTO();
        updatedHotel.setName(newName);
        updatedHotel.setAddress(newAddress);

        RestAssured
                .given()
                .header("Authorization", "Bearer " + adminToken) // Concatenate "Bearer " with userToken
                .contentType(ContentType.JSON)
                .body(updatedHotel)
                .when()
                .put("/hotels/{id}", hotelId)
                .then().log().all()
                .statusCode(expectedStatusCode)
                .body("name", equalTo(newName))
                .body("address", equalTo(newAddress))
                .body("id", equalTo(1));
    }


    @Test
    @DisplayName("Deleting an existing hotel with a specific hotel id")
    public void test7() {

        int hotelId = 1;
        int expectedStatusCode = 200;

        RestAssured
                .given()
                .header("Authorization", "Bearer " + adminToken) // Concatenate "Bearer " with userToken
                .contentType(ContentType.JSON)
                .pathParam("id", hotelId)
                .when()
                .delete("/hotels/{id}", hotelId)
                .then().log().all()
                .statusCode(expectedStatusCode);
    }

    @Test
    @DisplayName("Get all rooms")
    public void test8() {

        int expectedRoomsSize = 4;
        int expectedStatusCode = 200;


        List<RoomDTO> list = RestAssured
                .given()
                .header("Authorization", "Bearer " + adminToken) // Concatenate "Bearer " with userToken
                .contentType(ContentType.JSON)
                .when()
                .get("/rooms")
                .then()
                .statusCode(expectedStatusCode)
                .extract()
                .body()
                .jsonPath().getList("", RoomDTO.class);

        assertEquals(expectedRoomsSize, list.size());
    }

    @Test
    @DisplayName("Get specific room by id")
    public void test9() {

        int setId = 1;
        int expectedStatusCode = 200;

        RestAssured
                .given()
                .header("Authorization", "Bearer " + adminToken) // Concatenate "Bearer " with userToken
                .contentType(ContentType.JSON)
                .pathParam("id", setId)
                .when()
                .get("/rooms/{id}")
                .then()
                .statusCode(expectedStatusCode)
                .body("number", equalTo(1))
                .body("price", equalTo(500));
    }

    @Test
    @DisplayName("Create a new room")
    public void test10() {

        Room room = new Room();
        room.setNumber(3);
        room.setPrice(700);
        int expectedStatusCode = 200;

        RestAssured
                .given()
                .header("Authorization", "Bearer " + adminToken) // Concatenate "Bearer " with userToken
                .contentType(ContentType.JSON)
                .body(room)
                .when()
                .post("/rooms")
                .then().log().all()
                .statusCode(expectedStatusCode)
                .body("number", equalTo(3))
                .body("price", equalTo(700));
    }

    @Test
    @DisplayName("Update specific room by id")
    public void test11() {

        int setId = 1;
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setNumber(4);
        roomDTO.setPrice(1000);
        int expectedStatusCode = 200;

        RestAssured
                .given()
                .header("Authorization", "Bearer " + adminToken) // Concatenate "Bearer " with userToken
                .contentType(ContentType.JSON)
                .pathParam("id", setId)
                .body(roomDTO)
                .when()
                .put("/rooms/{id}")
                .then().log().all()
                .statusCode(expectedStatusCode)
                .body("number", equalTo(4))
                .body("price", equalTo(1000));
    }

    @Test
    @DisplayName("Delete specific room by id")
    public void test12() {

        int setId = 1;

        int expectedHotelId = 1;
        int expectedStatusCode = 200;

        RestAssured
                .given()
                .header("Authorization", "Bearer " + adminToken) // Concatenate "Bearer " with userToken
                .contentType(ContentType.JSON)
                .pathParam("id", setId)
                .when()
                .delete("/rooms/{id}")
                .then().log().all()
                .statusCode(expectedStatusCode)
                .body("number", equalTo(1))
                .body("price", equalTo(500))
                .body("hotelId", equalTo(expectedHotelId))
                .body("id", equalTo(expectedHotelId));
    }

    @Test
    @DisplayName("Access specific route via token user-role")
    void test(){

        String setUserName = "setUserName";
        String setUserPassword = "1234";

        String setBody = "{\"username\": \""+setUserName +"\", \"password\": \""+setUserPassword+"\"}";

        String response =
                RestAssured
                        .given()
                        .contentType(ContentType.JSON)
                        .body(setBody)
                        .when()
                        .post("/auth/register")
                        .then()
                        .statusCode(201)
                        .body("token", notNullValue())
                        .body("username", equalTo(setUserName))
                        .extract().asString();


        String token = JsonPath.from(response).getString("token");

        String expectedString = "Hello from USER Protected";
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                //.body(setBody) -> da vi ikke skal benytte POST sender vi ikke en body med
                .when()
                .get("/protected/user_demo")
                .then()
                .statusCode(200)
                .body("msg", equalTo(expectedString));
    }
}