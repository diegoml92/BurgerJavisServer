/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.entities;

public class OrderItem {
	
	private Product product;
	private int amount;
	
	public OrderItem(Product product, int amount) {
		this.product = product;
		this.amount = amount;
	}
	
	public OrderItem() {
		this(new Product(), 0);
	}

	public Product getProduct() {
		return product;
	}

	public int getAmount() {
		return amount;
	}

}
