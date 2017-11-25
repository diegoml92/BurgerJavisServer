/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.validation;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.burgerjavis.Common;
import com.burgerjavis.entities.User;

public class UserValidator {
	
	private static boolean validateUsername(String username) {
		return ValidationPatterns.USERNAME_PATTERN.matcher(username).matches();
	}
	
	private static boolean validatePassword(String password) {
		return password.length() >= Common.MIN_PASS_LENGTH;
	}
	
	private static boolean validateRoles(List<GrantedAuthority> roles) {
		return !roles.isEmpty();
	}
	
	public static boolean validateUser(User user) {
		return validateUsername(user.getUsername()) && 
				validatePassword(user.getPassword()) &&
				validateRoles(user.getRoles());
	}

}
