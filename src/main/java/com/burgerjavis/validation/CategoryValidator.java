/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.validation;

import com.burgerjavis.entities.Category;

public class CategoryValidator {
	
	private static boolean validateCategoryName(String name) {
		return ValidationPatterns.CATEGORY_NAME_PATTERN.matcher(name).matches();
	}
	
	public static boolean validateCategory(Category category) {
		return validateCategoryName(category.getName());
	}

}
