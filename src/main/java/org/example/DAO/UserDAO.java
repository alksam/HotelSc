package org.example.DAO;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.example.Config.HibernateConfig;
import org.example.Entity.Role;
import org.example.Entity.User;
import org.example.exceptions.EntityNotFoundException;

public class UserDAO implements ISecurityDAO{
    private static UserDAO instance;
    private static EntityManagerFactory emf;

    public static UserDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserDAO();
        }
        return instance;
    }



    @Override
    public User createUser(String username, String password) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User user = new User(username, password);
        Role userRole = em.find(Role.class, "user");
        if (userRole == null) {
            userRole = new Role("user");
            em.persist(userRole);
        }
        user.addRole(userRole);
        em.persist(user);
        em.getTransaction().commit();
        em.close();
        return user;
    }
    public User verifyUser(String username, String password) throws EntityNotFoundException {
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, username);
        if (user == null)
            throw new EntityNotFoundException("No user found with username: " + username);
        if (!user.verifyUser(password))
            throw new EntityNotFoundException("Wrong password");
        return user;
    }




    @Override
    public Role createRole(String role) {
        //return null;

        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public User addRoleToUser(String username, String role) {
        //return null;

        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static User findUser(String username) {
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, username);
        em.close();
        return user;
    }
    @Override
    public User addUserRole(String username, String role) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User foundUser = em.find(User.class, username);
            Role foundRole = em.find(Role.class, role);
            foundUser.addRole(foundRole);
            em.merge(foundUser);
            em.getTransaction().commit();
            return foundUser;
        } finally {
            em.close();
        }
    }
}