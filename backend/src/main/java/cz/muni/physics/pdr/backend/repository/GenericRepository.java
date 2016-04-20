package cz.muni.physics.pdr.backend.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
public interface GenericRepository<T, ID extends Serializable> {
    void insert(T entity) ;

    void delete(T entity) ;

    Collection<T> getAll() ;

    T getById(ID id) ;

    T searchFor(Predicate<T> predicate) ;

    Collection<T> searchForAll(Predicate<T> predicate) ;
}
