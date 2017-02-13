/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.burgerjavis.entities.Category;

public interface CategoryRepository extends CrudRepository<Category, String> {
	
	public List<Category> findByNameIgnoreCase(String name);
	
	public List<Category> findByFavoriteTrue();
	
}
