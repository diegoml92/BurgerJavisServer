/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.burgerjavis.entities.Product;

public interface ProductRepository extends CrudRepository<Product, String> {
	
	public List<Product> findByNameIgnoreCase (String name);
	
}
