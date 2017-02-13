/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.validation;

import java.util.List;

import com.burgerjavis.entities.Category;
import com.burgerjavis.entities.Ingredient;
import com.burgerjavis.entities.Product;

public class ProductValidator {
	
	public static boolean validateProductName(String name) {
		return ValidationPatterns.PRODUCT_NAME_PATTERN.matcher(name).matches();
	}
	
	public static boolean validateProductPrice(float price) {
		return price >= 0;
	}
	
	public static boolean validateProductCategory(Category category) {
		return CategoryValidator.validateCategory(category);
	}
	
	public static boolean validateProductIngredients(List<Ingredient> ingredients) {
		boolean valid = true;
		int i = 0;
		while(valid && i < ingredients.size()) {
			valid = IngredientValidator.validateIngredient(ingredients.get(i));
			i++;
		}
		return valid;
	}
	
	public static boolean validateProduct(Product product) {
		return validateProductName(product.getName()) && validateProductPrice(product.getPrice()) &&
				validateProductCategory(product.getCategory()) && 
				validateProductIngredients(product.getIngredients());
	}

}
