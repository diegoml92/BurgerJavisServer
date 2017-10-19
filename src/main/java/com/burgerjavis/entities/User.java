/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.entities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;


public class User {
	
	@Id
	private String _id;

	private String username;
	private String password;
	private List<GrantedAuthority> roles;
	
	public User(String username, String password, List<GrantedAuthority> roles) {
		this.username = username;
		this.password = password;
		this.roles = roles;
	}
	
	public User(String username, String password) {
		this(username, password, new ArrayList<GrantedAuthority>());
	}
	
	public User() {
		this("", "");
	}
	
	public String get_id() {
		return _id;
	}
	
	public void set_id(String _id) {
		this._id = _id;
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

	public List<GrantedAuthority> getRoles() {
		return roles;
	}

	public void setRoles(List<GrantedAuthority> roles) {
		this.roles = roles;
	}
	
	public boolean hasWaiterRole () {
		final String C_ROLE_WAITER = "ROLE_WAITER";
		final String C_ROLE_ADMIN = "ROLE_ADMIN";
		for (GrantedAuthority role : this.roles) {
			String roleAuth = role.getAuthority();
			if (roleAuth.equalsIgnoreCase(C_ROLE_WAITER) || roleAuth.equalsIgnoreCase(C_ROLE_ADMIN)) {
				return true;
			}
		}
		return false;
	}
	
}
