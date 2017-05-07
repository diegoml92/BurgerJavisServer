/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.entities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

public class Order {

	@Id
	private String _id;
	
	private String name;
	private List<OrderItem> items;
	private boolean finished;
	private String username;
		
	public Order(Order order) {
		this._id = order._id;
		this.name = order.name;
		this.items = order.items;
		this.finished = order.finished;
		this.username = order.username;
	}
	
	public Order(String name, List<OrderItem> items, boolean finished, String username) {
		this.name = name;
		this.items = items;
		this.finished = finished;
		this.username = username;
	}
	
	public Order(String name, List<OrderItem> items, String username) {
		this(name, items, false, username);
	}
	
	public Order(String name, String username) {
		this(name, new ArrayList<OrderItem>(), false, username);
	}
	
	public Order() {
		this("", new ArrayList<OrderItem>(), false, "");
	}
	
	
	public void updateOrder(Order order) {
		this.name = order.name;
		this.items = order.items;
		this.finished = order.finished;
	}
	
	public float calculatePrice() {
		float price = 0.0f;
		for(OrderItem item : this.items) {
			price += item.getProduct().getPrice() * item.getAmount();
		}
		return price;
	}
	
	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
