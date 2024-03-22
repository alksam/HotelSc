package org.example.Route;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.security.RouteRole;
import jakarta.persistence.EntityManagerFactory;
import lombok.*;
import org.example.Config.ApplicationConfig;
import org.example.Config.HibernateConfig;
import org.example.Controller.*;
import org.example.DAO.HotelDAO;
import org.example.DAO.RoomDAO;
import org.example.Entity.Hotel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Route {
    static DemoController demoController = new DemoController();
    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryConfig(false);
    private static IsecurityController securityController = new securityController(emf);
    private static RoomDAO roomDAO ;
    private static HotelDAO hotelDAO;

    private static ObjectMapper jsonMapper = new ObjectMapper();



    public static void main(String[] args) {
        startServer(7007);
    }


    public static void startServer(int port) {
        ObjectMapper om = new ObjectMapper();
        ApplicationConfig applicationConfig = ApplicationConfig.getInstance();
        applicationConfig
                .initiateServer()
                .startServer(port)
                .setExceptionHandlers()
                .checkSecurityRoles()
                .setRoute(roomRoutes())
                .setRoute(hotelRoutes())
                .setRoute(getSecurityRoutes())
                .setRoute(SecuredRoutes())
                .setRoute(() -> {
                    path("demo", () -> {
                        get("test", demoController.sayHello());
                        get("error", ctx -> {
                            throw new Exception("Dette er en test");
                        });
                    });
                });
    }




    public static void closeServer() {
        ApplicationConfig.getInstance().stopServer();
    }

    public static EndpointGroup getSecurityRoutes() {
        return ()->{
            path("/auth", ()->{
                post("/login", securityController.login(),Role.ANYONE);
                post("/register", securityController.register(),Role.ANYONE);
            });
        };
    }

    public static EndpointGroup SecuredRoutes(){
        return ()->{
            path("/protected", ()->{
                before(securityController.authenticate());
                get("/user_demo",(ctx)->ctx.json(jsonMapper.createObjectNode().put("msg",  "Hello from USER Protected")),Role.USER);
                get("/admin_demo",(ctx)->ctx.json(jsonMapper.createObjectNode().put("msg",  "Hello from ADMIN Protected")),Role.ADMIN);
            });
        };
    }
    public static EndpointGroup hotelRoutes() {
        return () -> {
            before(securityController.authenticate());
            get("/hotels", HotelController.getAll(hotelDAO), Role.USER);
            get("/hotels/{id}", HotelController.getHotelById(hotelDAO), Role.ADMIN);
            get("/hotels/{id}/rooms", HotelController.getHotelWithRooms(hotelDAO, roomDAO), Role.ANYONE);
            post("/hotels", HotelController.create(hotelDAO), Role.ANYONE);
            put("/hotels/{id}", HotelController.update(hotelDAO), Role.ANYONE);
            delete("/hotels/{id}", HotelController.delete(hotelDAO), Role.ANYONE);
        };
    }

    public static EndpointGroup roomRoutes() {
        return () -> {
            before(securityController.authenticate());
            get("/rooms", RoomController.getAll(roomDAO), Role.ADMIN);
            get("/rooms/{id}", RoomController.getRoom(roomDAO), Role.ADMIN);
            post("/rooms", RoomController.create(roomDAO), Role.ADMIN);
            put("/rooms/{id}", RoomController.update(roomDAO), Role.ADMIN);
            delete("/rooms/{id}", RoomController.delete(roomDAO), Role.ADMIN);
        };
    }



    private static enum Role implements RouteRole {
        ANYONE, USER, ADMIN
    }

}