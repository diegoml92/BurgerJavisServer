package com.burgerjavis.util;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.burgerjavis.entities.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UnitTestUtil {
	
	// HTTP CONTENT TYPE
	public static final MediaType APPLICATION_JSON_UTF8 =
		new MediaType (MediaType.APPLICATION_JSON.getType(),
				MediaType.APPLICATION_JSON.getSubtype(),
				Charset.forName("utf8"));
	
	// FLOAT COMPARISSON ERROR
	public static final double DELTA_ERROR = 0.0001;
	
	// ROLES
	public static enum UserRole {ROLE_ADMIN, ROLE_WAITER};
	
	private static final GrantedAuthority[] ADMIN = {new SimpleGrantedAuthority("ROLE_ADMIN")};
	private static final List<GrantedAuthority> ADMIN_ROLE = Arrays.asList(ADMIN);
	
	private static final GrantedAuthority[] WAITER = {new SimpleGrantedAuthority("ROLE_WAITER")};
	private static final List<GrantedAuthority> WAITER_ROLE = Arrays.asList(WAITER);
	
	// AUXILIARY METHODS
	public static String convertObjectToJson (Object object) {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(object);
	}
	
	public static User generateUser (String username, String password, UserRole role) {
		List<GrantedAuthority> roles;
		switch (role) {
			case ROLE_ADMIN: roles = ADMIN_ROLE; break;
			case ROLE_WAITER: roles = WAITER_ROLE; break;
			default: roles = new ArrayList<GrantedAuthority>();
		}
		return new User(username, new BCryptPasswordEncoder().encode(password), roles);
	}
	
}
