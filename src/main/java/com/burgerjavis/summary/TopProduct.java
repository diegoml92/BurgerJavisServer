/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.summary;

public class TopProduct {
	
	private String productName;
	private int amount;

	public TopProduct(String productName, int amount) {
		this.productName = productName;
		this.amount = amount;
	}
	
	public void increaseAmount(int amount) {
		this.amount += amount;
	}

	public String getProductName() {
		return productName;
	}

	public int getAmount() {
		return amount;
	}
	
}
