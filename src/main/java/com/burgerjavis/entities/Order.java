/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.entities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.burgerjavis.Common.OrderState;

public class Order {

	@Id
	private String _id;
	
	private String name;
	private List<OrderItem> items;
	private OrderState state;
	private String username;
		
	public Order(Order order) {
		this._id = order._id;
		this.name = order.name;
		this.items = order.items;
		this.state = order.state;
		this.username = order.username;
	}
	
	public Order(String name, List<OrderItem> items, OrderState state, String username) {
		this.name = name;
		this.items = items;
		this.state = state;
		this.username = username;
	}
	
	public Order(String name, List<OrderItem> items, String username) {
		this(name, items, OrderState.INITIAL, username);
	}
	
	public Order(String name, String username) {
		this(name, new ArrayList<OrderItem>(), username);
	}
	
	public Order() {
		this("", "");
	}
	
	
	public void updateOrder(Order order) {
		this.name = order.name;
		this.items = order.items;
		this.state = order.state;
	}
	
	public float calculatePrice() {
		float price = 0.0f;
		for(OrderItem item : this.items) {
			price += item.getProduct().getPrice() * item.getAmount();
		}
		return price;
	}
	
	public boolean isState(OrderState state) {
		return state.equals(this.state);
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

	public OrderState getState() {
		return state;
	}

	public void setState(OrderState state) {
		this.state = state;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
