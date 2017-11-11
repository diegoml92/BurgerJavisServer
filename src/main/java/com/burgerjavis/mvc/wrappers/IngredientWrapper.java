/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.mvc.wrappers;

import com.burgerjavis.entities.Ingredient;

public class IngredientWrapper implements Wrapper<Ingredient> {
	
	private String id;
	private String name;
	private float extraPrice;
	
	@Override
	public Ingredient getInternalType() {
		Ingredient ingredient = new Ingredient();
		ingredient.set_id(id);
		ingredient.setName(name);
		ingredient.setExtraPrice(extraPrice);
		return ingredient;
	}
	
	@Override
	public void wrapInternalType(Ingredient param) {
		this.id = param.get_id();
		this.name = param.getName();
		this.extraPrice = param.getExtraPrice();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

}
