/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis;

import com.burgerjavis.entities.User;

public class Credentials {
	
	private String username;
	private String password;
	
	public Credentials(User user) {
		this.username = user.getUsername();
		this.password = user.getPassword();
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
