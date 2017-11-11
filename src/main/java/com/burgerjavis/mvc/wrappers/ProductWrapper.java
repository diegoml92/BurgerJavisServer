/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.mvc.wrappers;

import java.util.ArrayList;
import java.util.List;

import com.burgerjavis.entities.Category;
import com.burgerjavis.entities.Ingredient;
import com.burgerjavis.entities.Product;
import com.burgerjavis.services.CategoryAccessor;

public class ProductWrapper implements Wrapper<Product> {
	
	private String id;
	private String name;
	private float price;
	private String categoryId;
	private List<IngredientWrapper> ingredients;
	
	@Override
	public Product getInternalType() {
		Product product = new Product();
		product.set_id(id);
		product.setName(name);
		product.setPrice(price);
		if(CategoryAccessor.categoryRepository == null) System.err.println("NOOOOOOOOOOOOOOOO!!!!");
		Category category = CategoryAccessor.categoryRepository.findOne(categoryId);
		if(category == null) {
			category = new Category();
		}
		product.setCategory(category);
		List<Ingredient> ingredientList = new ArrayList<Ingredient>();
		for(IngredientWrapper ingredient : ingredients) {
			ingredientList.add(ingredient.getInternalType());
		}
		product.setIngredients(ingredientList);
		return product;
	}
	
	@Override
	public void wrapInternalType(Product param) {
		this.id = param.get_id();
		this.name = param.getName();
		this.price = param.getPrice();
		this.categoryId = param.getCategory().get_id();
		this.ingredients = new ArrayList<IngredientWrapper>();
		for(Ingredient ingredient : param.getIngredients()) {
			IngredientWrapper ingredientWrapper = new IngredientWrapper();
			ingredientWrapper.wrapInternalType(ingredient);
			ingredients.add(ingredientWrapper);
		}
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

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public List<IngredientWrapper> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<IngredientWrapper> ingredients) {
		this.ingredients = ingredients;
	}

}
