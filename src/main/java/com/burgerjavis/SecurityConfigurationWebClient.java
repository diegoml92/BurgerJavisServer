/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableGlobalMethodSecurity (securedEnabled = true)
@Order(2)
public class SecurityConfigurationWebClient extends WebSecurityConfigurerAdapter {
	
	@Autowired
	public CustomAuthenticationProvider authenticationProvider;
	
	@Override
	protected void configure (HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/webclient/login").permitAll()
			.anyRequest().hasRole("ADMIN");
		
		http.formLogin().loginPage("/webclient/login").defaultSuccessUrl("/", true)
			.failureUrl("/webclient/login?error").permitAll();
		
		http.logout().logoutUrl("/webclient/logout").logoutSuccessUrl("/webclient/login?logout").permitAll();
	}
	
	@Override
	protected void configure (AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider);
	}
	
}
