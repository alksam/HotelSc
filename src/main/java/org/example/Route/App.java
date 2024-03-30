package org.example.Route;

import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;
import org.example.Config.ApplicationConfig;
import org.example.Config.HibernateConfig;
import org.example.DAO.HotelDAO;
import org.example.DAO.RoomDAO;
import org.example.DAO.UserDAO;

public class App {


    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryConfig(false);
    private static HotelDAO hotelDAO = HotelDAO.getInstance(emf);
    private static RoomDAO roomDAO = RoomDAO.getInstance(emf);
    private static UserDAO userDAO = UserDAO.getInstance(emf);

    private static Route route = new Route();

    // Declare a public static method named addRoutes which returns an EndpointGroup
    public static EndpointGroup addRoutes() {
        // Call the combineRoutes method passing the EndpointGroup instances returned by routeHotel.hotelRoutes() and roomRoute.roomRoutes()
        return combineRoutes(  route.hotelRoutes(), route.roomRoutes(), route.getSecurityRoutes(), route.SecuredRoutes());
    }

    // Define a private static method named combineRoutes which takes multiple EndpointGroup instances as arguments
    private static EndpointGroup combineRoutes(EndpointGroup... endpointGroups) {
        // Define a lambda expression for the EndpointGroup
        return () -> {
            // Iterate through each EndpointGroup passed as arguments
            for (EndpointGroup group : endpointGroups) {
                // Add the endpoints from each EndpointGroup
                group.addEndpoints();
            }
        };
    }


  /* public static void main(String[] args) {
    ApplicationConfig app = ApplicationConfig.getInstance();
    app.initiateServer()
            .startServer()
            .setExceptionHandlers()
            .checkSecurityRoles()
            .setRoute(addRoutes());
}*/


}
