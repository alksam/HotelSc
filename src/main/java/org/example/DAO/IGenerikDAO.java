package org.example.DAO;

import java.util.List;

public interface IGenerikDAO<T> {
    List<T> getAll();
    T getById(int id);
    T create(T entity);
    int update(T entity);
    int delete(int id);
}
