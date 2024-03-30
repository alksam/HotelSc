

import io.javalin.http.ContentType;
import io.restassured.RestAssured;
import io.restassured.RestAssured.*;
import io.restassured.matcher.RestAssuredMatchers.*;
import io.restassured.response.Response;
import org.example.Config.ApplicationConfig;
import org.example.Config.HibernateConfig;
import org.example.Controller.HotelController;
import org.example.DAO.HotelDAO;
import org.example.Route.App;
import org.example.Route.Route;
import org.hamcrest.Matchers.*;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;



import jakarta.persistence.EntityManagerFactory;
public class EksamelTEST {

    private static final String BASE_URL = "http://localhost:7040/api";
    private static HotelController hotelController;
    private static EntityManagerFactory emf;
    private static ApplicationConfig app;
    private static Integer port = 7040;
    private static String userUsername;
    private static String adminToken;
    private static String adminUsername;

    private static App appi = new App();

    private static HotelDAO hotelDAO;


    private static Route route ;

    @BeforeAll
    public static void setUp() {

        emf = HibernateConfig.getEntityManagerFactoryConfig(true);
        hotelController = new HotelController(emf);


        App appi = new App();

        app = ApplicationConfig.getInstance();
        app.initiateServer()
                .startServer(port)
                .setExceptionHandlers()
                .checkSecurityRoles()
                .setRoute(appi.addRoutes());
    }

    @AfterAll
    public static void tearDown() {
        emf.close();
        app.stopServer();
    }

    @BeforeEach
    public void setup() {
        hotelDAO = HotelDAO.getInstance(emf);
        hotelController = new HotelController(emf);

        String requestBody = "{\"username\": \"" + userUsername + "\", \"password\": \"" + "1234" + "\"}";
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/auth/register");
        // Assert status code is 201
        response.then().assertThat().statusCode(201);
    }








    @AfterEach

    @Test
    public void testGetAllHotels() {
        given()
                .when()
                .get("/api/hotels")
                .then()
                .statusCode(200);
        // Add more assertions as needed
    }

    @Test
    public void testGetHotelById() {
        given()
                .pathParam("id", 1)
                .when()
                .get("/api/hotels/{id}")
                .then()
                .statusCode(200);
        // Add more assertions as needed
    }





}
