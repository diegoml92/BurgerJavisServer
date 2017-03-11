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
	private float extraPrice;
	
	public Ingredient(Ingredient ingredient) {
		this._id = ingredient._id;
		this.name = ingredient.name;
		this.extraPrice = ingredient.extraPrice;
	}
	
	public Ingredient(String name, float extraPrice) {
		this.name = name;
		this.extraPrice = extraPrice;
	}
	
	public Ingredient(String name) {
		this(name, 0.0f);
	}
	
	public Ingredient() {
		this("", 0);
	}
	
	public void updateIngredient(Ingredient ingredient) {
		this.name = ingredient.name;
		this.extraPrice = ingredient.extraPrice;
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

	public float getExtraPrice() {
		return extraPrice;
	}

	public void setExtraPrice(float extraPrice) {
		this.extraPrice = extraPrice;
	}
	
	@Override
	public String toString() {
		return this.name + " (" + this.extraPrice + ")";
	}

}
