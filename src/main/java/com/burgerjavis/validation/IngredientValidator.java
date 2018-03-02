/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.validation;

import com.burgerjavis.entities.Ingredient;

public class IngredientValidator {
	
	private static boolean validateIngredientName(String name) {
		return ValidationPatterns.INGREDIENT_NAME_PATTERN.matcher(name).matches();
	}
	
	public static boolean validateIngredient(Ingredient ingredient) {
		return validateIngredientName(ingredient.getName());
	}

}
