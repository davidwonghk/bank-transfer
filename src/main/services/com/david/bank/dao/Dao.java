package com.david.bank.dao;

import java.util.List;

/**
 * Data Access Object Interface for accessing the domain data
 * @param <T>  the Class of the domain object
 * @param <ID> the type of the domain object unique id
 */
public interface Dao<T, ID> {
	T get(ID id);

	List<T> getAll();

	T save(T t);

	void delete(T t);


	class EntityNotFoundException extends RuntimeException {
		public EntityNotFoundException(Class clazz, String name) {
			super("Entity '" + name + "' of " + clazz.getSimpleName() + " is not found");
		}
	}

}
