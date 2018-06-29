/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.entities;

import org.springframework.data.annotation.Id;

public class Ingredient {
	
	@Id
	private String _id;

	private String name;
	
	public Ingredient(Ingredient ingredient) {
		this._id = ingredient._id;
		this.name = ingredient.name;
	}
	
	public Ingredient(String name) {
		this.name = name;
	}
	
	public Ingredient() {
		this("");
	}
	
	public void updateIngredient(Ingredient ingredient) {
		this.name = ingredient.name;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
