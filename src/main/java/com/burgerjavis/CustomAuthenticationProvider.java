/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis;

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
public class CustomAuthenticationProvider implements AuthenticationProvider {
	
	@Autowired
	private UserRepository userRepository;
	
	private boolean isAdmin(List<GrantedAuthority> roles) {
		boolean admin = false;
		int i = 0;
		while (!admin && i < roles.size()) {
			admin = roles.get(i).getAuthority().equalsIgnoreCase(Common.ADMIN_ROLE);
			i++;
		}
		return admin;
	}
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = (String) authentication.getCredentials();
		User user = userRepository.findByUsernameIgnoreCase(username);
		if (user == null) {
			throw new BadCredentialsException ("User not found");
		}
		if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {
			throw new BadCredentialsException ("Wrong password");
		}
		List<GrantedAuthority> roles = user.getRoles();
		if (!isAdmin(roles)) {
			throw new BadCredentialsException("Wrong permissions");
		}
		return new UsernamePasswordAuthenticationToken(username, password, roles);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
