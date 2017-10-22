/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.summary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.burgerjavis.entities.Category;
import com.burgerjavis.entities.Order;
import com.burgerjavis.entities.OrderItem;

public class SummaryData {
	
	private float profits;
	private int completedOrders;
	private List<Category> topCategories;
	private List<List<TopProduct>> topProducts;
	
	private Map<String, SummaryData> userSummaryData;
	
	private int counter = 0;
	
	public SummaryData() {
		this.completedOrders = 0;
		this.profits = 0.0f;
		this.topCategories = new ArrayList<Category>();
		this.topProducts = new ArrayList<List<TopProduct>>();
		this.userSummaryData = new HashMap<String, SummaryData>();
	}
	
	public SummaryData(List<Order> orders, List<Category> categories) {
		this.completedOrders = orders.size();
		this.profits = 0.0f;
		this.topCategories = new ArrayList<Category>(categories);
		this.topCategories.add(new Category("- Otros -"));
		this.topProducts = new ArrayList<List<TopProduct>>();
		this.userSummaryData = new HashMap<String, SummaryData>();
		// Create a top product list per category
		for (int i = 0; i < this.topCategories.size(); i++) {
			this.topProducts.add(new ArrayList<TopProduct>());
		}
		// Count the amount of every single product and calculate the profits
		for(Order order : orders) {
			this.profits += order.calculatePrice();
			for(OrderItem item : order.getItems()) {
				String productName = item.getProduct().getName();
				int amount = item.getAmount();
				boolean found = false;
				int i = 0;
				// For each category...
				while(!found && i < this.topProducts.size()) {
					int j = 0;
					// ...look for the current product
					while(!found && j < this.topProducts.get(i).size()) {
						found = productName.equalsIgnoreCase(this.topProducts.get(i).get(j).getProductName());
						if(found) {
							this.topProducts.get(i).get(j).increaseAmount(amount);
						}
						j++;
					}
					i++;
				}
				// If the product does not exist, add it to the corresponding category
				if(!found) {
					boolean categoryFound = false;
					int k = 0;
					while (!categoryFound && k < this.topCategories.size() - 1) {
						categoryFound = item.getProduct().getCategory().getName()
									.equalsIgnoreCase(this.topCategories.get(k).getName());
						if(categoryFound) {
							this.topProducts.get(k).add(new TopProduct(productName, amount));
						}
						k++;
					}
					if(!categoryFound) {
						this.topProducts.get(this.topCategories.size() - 1)
							.add(new TopProduct(productName, amount));
					}
				}
			}
		}
		// Ignore empty categories
		for(int index = this.topProducts.size() - 1; index >= 0; index--) {
			if(this.topProducts.get(index).isEmpty()) {
				this.topProducts.remove(index);
				this.topCategories.remove(index);
			} else {
				// Sort the given list
				this.topProducts.get(index).sort((p1,p2) -> p2.getAmount() - p1.getAmount());
			}
		}
	}
	
	public void resetCounter() {
		this.counter = 0;
	}
	
	public int getNextCounter() {
		return this.counter++;
	}
	
	public SummaryData getUserData(String username) {
		SummaryData data = this.userSummaryData.get(username);
		return data != null ? data : new SummaryData();
	}
	
	public void setUserData(String username, SummaryData data) {
		this.userSummaryData.put(username, data);
	}

	public float getProfits() {
		return profits;
	}

	public void setProfits(float profits) {
		this.profits = profits;
	}

	public int getCompletedOrders() {
		return completedOrders;
	}

	public void setCompletedOrders(int completedOrders) {
		this.completedOrders = completedOrders;
	}

	public List<Category> getTopCategories() {
		return topCategories;
	}

	public void setTopCategories(List<Category> topCategories) {
		this.topCategories = topCategories;
	}

	public List<List<TopProduct>> getTopProducts() {
		return topProducts;
	}

	public void setTopProducts(List<List<TopProduct>> topProducts) {
		this.topProducts = topProducts;
	}

	public Map<String, SummaryData> getUserSummaryData() {
		return userSummaryData;
	}

	public void setUserSummaryData(Map<String, SummaryData> userSummaryData) {
		this.userSummaryData = userSummaryData;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
	
}
