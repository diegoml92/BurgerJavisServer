/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.entities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

public class Product {
	
	@Id
	private String _id;

	private String name;
	private float price;
	private Category category;
	private List<Ingredient> ingredients;
	
	public Product(Product product) {
		this._id = product._id;
		this.name = product.name;
		this.price = product.price;
		this.category = product.category;
		this.ingredients = product.ingredients;
	}
	
	public Product(String name, float price, Category category, List<Ingredient> ingredients) {
		this.name = name;
		this.price = price;
		this.category = category;
		this.ingredients = ingredients;
	}
	
	public Product(String name, float price, Category category) {
		this(name, price, category, new ArrayList<Ingredient>());
	}
	
	public Product (String name, float price, List<Ingredient> ingredients) {
		this(name, price, new Category(), ingredients);
	}
	
	public Product(String name, float price) {
		this(name, price, new Category(), new ArrayList<Ingredient>());
	}
	
	public Product() {
		this("", 0, new Category(), new ArrayList<Ingredient>());
	}
	
	public void updateProduct (Product product) {
		this.name = product.name;
		this.price = product.price;
		this.category = product.category;
		this.ingredients = product.ingredients;
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
	
	public float getPrice() {
		return price;
	}
	
	public void setPrice(float price) {
		this.price = price;
	}
	
	public Category getCategory() {
		return category;
	}
	
	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

	@Override
	public String toString() {
		String result = this.name + " " + this.price + "[" + this.category.toString() + "]\n";
		if (!this.ingredients.isEmpty()) {
			for (Ingredient ing : this.ingredients) {
				result += "\t" + ing.toString();
			}
		} else {
			result += "No hay ingredientes";
		}
		return result;
	}
	
	
}
