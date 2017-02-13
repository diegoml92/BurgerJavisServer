/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.validation;

import java.util.regex.Pattern;

public final class ValidationPatterns {
	
	public static final Pattern ORDER_NAME_PATTERN = 
			Pattern.compile("[a-zA-ZñÑ0-9][a-zA-ZñÑ 0-9]*");
	
	public static final Pattern PRODUCT_NAME_PATTERN = 
			Pattern.compile("[a-zA-ZñÑ][a-zA-ZñÑ ]*");
	
	public static final Pattern INGREDIENT_NAME_PATTERN = 
			Pattern.compile("[a-zA-ZñÑ][a-zA-ZñÑ ]*");
	
	public static final Pattern CATEGORY_NAME_PATTERN =
			Pattern.compile(".*");
	
	public static final Pattern CATEGORY_ICON_PATTERN =
			Pattern.compile(".*");

}
