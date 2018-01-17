/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.mvc.wrappers;

import java.util.Arrays;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.burgerjavis.entities.User;

public class UserWrapper implements Wrapper<User> {
	
	private String id;
	private String username;
	private String password;
	private String role;

	@Override
	public User getInternalType() {
		User user = new User();
		user.set_id(this.id);
		user.setUsername(this.username);
		user.setPassword(this.password);
		GrantedAuthority [] roles = { new SimpleGrantedAuthority(this.role) };
		user.setRoles(Arrays.asList(roles));
		return user;
	}

	@Override
	public void wrapInternalType(User param) {
		this.id = param.get_id();
		this.username = param.getUsername();
		this.password = param.getPassword();
		this.role = param.getRoles().get(0).getAuthority();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
