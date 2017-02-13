/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.burgerjavis.entities.Ingredient;

public interface IngredientRepository extends CrudRepository<Ingredient, String> {
	
	public List<Ingredient> findByNameIgnoreCase(String name);
	
}
