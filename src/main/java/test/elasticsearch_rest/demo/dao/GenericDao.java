package test.elasticsearch_rest.demo.dao;

import java.io.Serializable;
import java.util.List;

public interface GenericDao<T, PK extends Serializable> {

    void create(T newInstance);

    List<T> findAll();

    T findById(PK id);
    
    T update(T transientObject);

    void delete(T persistentObject);
    
}