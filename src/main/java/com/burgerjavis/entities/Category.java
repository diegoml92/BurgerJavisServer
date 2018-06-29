/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.entities;

import org.springframework.data.annotation.Id;

public class Category {
	
	@Id
	private String _id;
	
	private String name;
	private boolean favorite;
	
	public Category(Category category) {
		this._id = category._id;
		this.name = category.name;
		this.favorite = category.favorite;
	}
	
	public Category(String name, boolean favorite) {
		this.name = name;
		this.favorite = favorite;
	}
	
	public Category(String name) {
		this(name, false);
	}
	
	public Category() {
		this("", false);
	}
	
	public void updateCategory(Category category) {
		this.name = category.name;
		this.favorite = category.favorite;
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
	
	public boolean isFavorite() {
		return favorite;
	}
	
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

}
