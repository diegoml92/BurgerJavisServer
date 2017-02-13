/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.validation;

import com.burgerjavis.entities.Category;

public class CategoryValidator {
	
	public static boolean validateCategoryName(String name) {
		return ValidationPatterns.CATEGORY_NAME_PATTERN.matcher(name).matches();
	}
	
	public static boolean validateCategoryIcon(String icon) {
		return ValidationPatterns.CATEGORY_ICON_PATTERN.matcher(icon).matches();
	}
	
	public static boolean validateCategory(Category category) {
		return validateCategoryName(category.getName()) && validateCategoryIcon(category.getIcon());
	}

}
