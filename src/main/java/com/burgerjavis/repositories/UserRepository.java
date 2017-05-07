/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.burgerjavis.entities.User;

public interface UserRepository extends CrudRepository<User, String> {
	
	public List<User> findByUsernameIgnoreCase (String username);

}
