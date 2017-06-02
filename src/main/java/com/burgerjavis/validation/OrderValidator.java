/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.validation;

import java.util.List;

import com.burgerjavis.entities.Order;
import com.burgerjavis.entities.OrderItem;

public class OrderValidator {
	
	private static boolean validateOrderName(String name) {
		name = name.trim();
		return ValidationPatterns.ORDER_NAME_PATTERN.matcher(name).matches();
	}
	
	private static boolean validateOrderItems(List<OrderItem> items) {
		boolean valid = true;
		int i = 0;
		while (valid && i < items.size()) {
			valid = OrderItemValidator.validateOrderItem(items.get(i));
			i++;
		}
		return valid;
	}
	
	private static boolean validateOrderUsername(String username) {
		username = username.trim();
		return ValidationPatterns.USERNAME_PATTERN.matcher(username).matches();
	}
	
	public static boolean validateOrder(Order order) {
		return validateOrderName(order.getName()) && validateOrderItems(order.getItems()) 
				&& validateOrderUsername(order.getUsername());
	}

}
