package org.example.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.config.JavalinConfig;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;
import org.example.Controller.IsecurityController;
import org.example.Controller.securityController;
import org.example.Route.App;
import org.example.Route.Route;
import org.example.dtos.UserDTO;
import org.example.exceptions.ApiException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationConfig {
    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryConfig(false);
    private IsecurityController securityController = new securityController(emf);
    private ObjectMapper om = new ObjectMapper();
    private static ApplicationConfig instance;
    private static Javalin app;
    private static Route routes;



        private ApplicationConfig() {
            // Initialize ObjectMapper
            ObjectMapper om = new ObjectMapper();

            // Initialize Javalin app
            app = Javalin.create(config -> {
                config.http.defaultContentType = "application/json";
                config.routing.contextPath = "/api";
            });
        }

        public static ApplicationConfig getInstance() {
            if (instance == null) {
                instance = new ApplicationConfig();
            }
            return instance;
        }

        public ApplicationConfig initiateServer() {
            return instance;
        }

        public ApplicationConfig startServer(int portNumber) {
            app.start(portNumber);
            return instance;
        }

        public ApplicationConfig setRoute(EndpointGroup route) {
            app.routes(route);
            return instance;
        }

        public ApplicationConfig setExceptionHandlers() {

            app.exception(Exception.class, (e, ctx) -> {
                ObjectNode node = om.createObjectNode().put("errorMessage", e.getMessage());
                ctx.status(500).json(node);
            });

            app.error(404, ctx -> {
                ctx.status(404).result("Not Found");
            });

            app.exception(IllegalStateException.class, (e, ctx) -> {
                ctx.status(400).result("Bad Request: " + e.getMessage());
            });

            return instance;
        }

        public ApplicationConfig checkSecurityRoles() {
            // Check roles on the user (ctx.attribute("username") and compare with permittedRoles using securityController.authorize()
            app.updateConfig(config -> {

                config.accessManager((handler, ctx, permittedRoles) -> {
                    // permitted roles are defined in the last arg to routes: get("/", ctx -> ctx.result("Hello World"), Role.ANYONE);

                    Set<String> allowedRoles = permittedRoles.stream().map(role -> role.toString().toUpperCase()).collect(Collectors.toSet());
                    if (allowedRoles.contains("ANYONE") || ctx.method().toString().equals("OPTIONS")) {
                        // Allow requests from anyone and OPTIONS requests (preflight in CORS)
                        handler.handle(ctx);
                        return;
                    }

                    UserDTO user = ctx.attribute("user");
                    System.out.println("USER IN CHECK_SEC_ROLES: " + user);
                    if (user == null)
                        ctx.status(HttpStatus.FORBIDDEN)
                                .json(om.createObjectNode()
                                        .put("msg", "Not authorized. No username were added from the token"));

                    if (securityController.authorize(user, allowedRoles))
                        handler.handle(ctx);
                    else
                        throw new ApiException(HttpStatus.FORBIDDEN.getCode(), "Unauthorized with roles: " + allowedRoles);
                });
            });
            return instance;
        }


        public void stopServer() {
            app.stop();
        }
    }
