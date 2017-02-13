/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.validation;

import com.burgerjavis.entities.Ingredient;

public class IngredientValidator {
	
	public static boolean validateIngredientName(String name) {
		return ValidationPatterns.INGREDIENT_NAME_PATTERN.matcher(name).matches();
	}
	
	public static boolean validateIngredientExtraPrice(float extraPrice) {
		return extraPrice >= 0.0;
	}
	
	public static boolean validateIngredient(Ingredient ingredient) {
		return validateIngredientName(ingredient.getName()) && 
				validateIngredientExtraPrice(ingredient.getExtraPrice());
	}

}
