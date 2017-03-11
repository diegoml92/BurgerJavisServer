/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.validation;

import com.burgerjavis.entities.OrderItem;
import com.burgerjavis.entities.Product;

public class OrderItemValidator {
	
	private static boolean validateOrderItemAmount (int amount) {
		return amount >= 0;
	}
	
	private static boolean validateOrderItemProduct(Product product) {
		return ProductValidator.validateProduct(product);
	}
	
	public static boolean validateOrderItem(OrderItem item) {
		return validateOrderItemAmount(item.getAmount()) &&
				validateOrderItemProduct(item.getProduct());
	}

}
