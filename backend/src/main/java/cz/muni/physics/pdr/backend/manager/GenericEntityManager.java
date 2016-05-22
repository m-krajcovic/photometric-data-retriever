package cz.muni.physics.pdr.backend.manager;

import java.io.Serializable;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
public interface GenericEntityManager<T, ID extends Serializable> {
    /**
     * Method for storing entity
     * @param entity entity to be stored
     */
    void insert(T entity);

    /**
     * Method for removing entity
     * @param entity entity to be removed
     */
    void delete(T entity);

    /**
     * This method returns all available entities
     * @return all available entities
     */
    Collection<T> getAll();

    /**
     * This method is for getting first entity that matches given predicate
     * @param predicate predicate to match entity
     * @return first entity that matches predicate
     */
    T searchFor(Predicate<T> predicate);

    /**
     * This method is for retrieving all entities that match given predicate
     * @param predicate predicate to match entity
     * @return all entities that match predicate
     */
    Collection<T> searchForAll(Predicate<T> predicate);

    /**
     * This method retrieves entity by its ID
     * @param id id for identifying entity
     * @return found entity by its id
     */
    T getById(ID id);
}
