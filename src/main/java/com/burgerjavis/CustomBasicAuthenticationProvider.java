/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.burgerjavis.entities.User;
import com.burgerjavis.repositories.UserRepository;

@Component
public class CustomBasicAuthenticationProvider implements AuthenticationProvider {
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = (String) authentication.getCredentials();
		List<User> users = userRepository.findByUsernameIgnoreCase(username);
		if (users.isEmpty()) {
			throw new BadCredentialsException ("User not found");
		}
		if (!new BCryptPasswordEncoder().matches(password, users.get(0).getPassword())) {
			throw new BadCredentialsException ("Wrong password");
		}
		//List<GrantedAuthority> roles = user.getRoles();
		List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
		return new UsernamePasswordAuthenticationToken(username, password, roles);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
