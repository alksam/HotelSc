package org.example.DAO;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public abstract class AbstractDAO<T> implements IGenerikDAO<T> {

    public EntityManagerFactory emf;
    private Class<T> entityClass;

    public AbstractDAO(EntityManagerFactory emf, Class<T> entityClass) {
        this.emf = emf;
        this.entityClass = entityClass;
    }

    @Override
    public T create(T entity) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        } finally {
            em.close();
        }
    }

    @Override
    public int update(T entity) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
            return 1;
        } finally {
            em.close();
        }
    }

    @Override
    public int delete(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            T entityToDelete = em.find(entityClass, id);

            if (entityToDelete != null) {
                em.remove(entityToDelete);
                em.getTransaction().commit();
                return 1;
            }
            return 0;
        } finally {
            em.close();
        }
    }

    @Override
    public T getById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }
}