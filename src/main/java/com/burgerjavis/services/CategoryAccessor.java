/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.services;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.burgerjavis.repositories.CategoryRepository;

@Component
public class CategoryAccessor {

	public static CategoryRepository categoryRepository;
	
	@Autowired
	public CategoryRepository categoryRepository0;

	@PostConstruct
	private void initStaticRepo() {
		categoryRepository = this.categoryRepository0;
	}
}
