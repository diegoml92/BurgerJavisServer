/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.burgerjavis.entities.User;
import com.burgerjavis.repositories.UserRepository;

@Component
public class DatabaseLoader {
	
	// REQUIRED REPOSITORIES
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private UserRepository userRepository;
	
	@PostConstruct
	private void initDatabase() {
				
		if (mongoTemplate.getDb().getCollection("user").find().size() == 0) {
			System.out.println("INITIALIZE EMPTY DB -> CREATING ADMIN USER...");
			GrantedAuthority [] roleAdmin = { new SimpleGrantedAuthority(Common.ADMIN_ROLE) };
			userRepository.save(new User("admin", new BCryptPasswordEncoder().encode("admin"),
											Arrays.asList(roleAdmin)));
		}
		
	}

}
