package cz.muni.physics.pdr.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 14/04/16
 */
public interface GenericRepository<T, ID extends Serializable> {
    void insert(T entity);

    void delete(T entity);

    Collection<T> getAll();

    T getById(ID id);

    default T searchFor(Predicate<T> predicate) {
        return getAll().stream().filter(predicate).findFirst().orElseGet(() -> null);
    }

    default Collection<T> searchForAll(Predicate<T> predicate) {
        return getAll().stream().filter(predicate).collect(Collectors.toList());
    }
}
