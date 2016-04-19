package cz.muni.physics.pdr.backend.manager;

import cz.muni.physics.pdr.backend.exception.ResourceAvailabilityException;

import java.io.Serializable;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
public interface GenericEntityManager<T, ID extends Serializable> {
    void insert(T entity) throws ResourceAvailabilityException;

    void delete(T entity) throws ResourceAvailabilityException;

    Collection<T> getAll() throws ResourceAvailabilityException;

    T searchFor(Predicate<T> predicate) throws ResourceAvailabilityException;

    Collection<T> searchForAll(Predicate<T> predicate) throws ResourceAvailabilityException;

    T getById(ID id) throws ResourceAvailabilityException;
}
