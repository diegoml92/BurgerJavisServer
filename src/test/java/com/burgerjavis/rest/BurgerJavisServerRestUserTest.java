/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.burgerjavis.MongoTestConfiguration;
import com.burgerjavis.entities.User;
import com.burgerjavis.repositories.UserRepository;
import com.burgerjavis.util.UnitTestUtil;
import com.burgerjavis.util.UnitTestUtil.UserRole;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={MongoTestConfiguration.class})
@WebAppConfiguration
public class BurgerJavisServerRestUserTest {
	
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private UserRepository userRepository;
	
	private MockMvc mockMvc;

	@Before
	public void setUp() throws Exception {
		mongoTemplate.getDb().dropDatabase();
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
					.apply(springSecurity())
					.build();
	}
	
	@After
	public void tearDown() throws Exception {
		mongoTemplate.getDb().dropDatabase();
	}
	
	@Test
	public void testGetUser() throws Exception {
		
		// Initialize database
		User u1 = new User("user1", new BCryptPasswordEncoder().encode("pass"));
		userRepository.save(u1);
		
		mockMvc.perform(get("/appclient/users/thisUsernameDoesNotExist"))
			.andExpect(status().isNotFound());
		
		mockMvc.perform(get("/appclient/users/USER1"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.username", is(u1.getUsername())))
			.andExpect(jsonPath("$.password", is(u1.getPassword())));
		
	}
	
	@Test
	public void testGetUsernames() throws Exception {
		
		// Initialize database
		final String PASSWORD = "pass";
		
		User u1 = UnitTestUtil.generateUser("user1", PASSWORD, UserRole.ROLE_WAITER);
		User u2 = UnitTestUtil.generateUser("user2", PASSWORD, UserRole.ROLE_WAITER);
		User u3 = UnitTestUtil.generateUser("user3", PASSWORD, UserRole.ROLE_KITCHEN);
		User u4 = UnitTestUtil.generateUser("admin", PASSWORD, UserRole.ROLE_ADMIN);
		
		userRepository.save(u1);
		userRepository.save(u2);
		userRepository.save(u3);
		userRepository.save(u4);
		
		RequestPostProcessor httpBasicHeader = httpBasic(u1.getUsername(), PASSWORD);
		RequestPostProcessor httpBasicHeaderAdmin = httpBasic(u4.getUsername(), PASSWORD);
		RequestPostProcessor httpBasicHeaderNotExists = httpBasic("NonExistingUser", PASSWORD);
		
		// This user does not exist
		mockMvc.perform(get("/appclient/users/username").with(httpBasicHeaderNotExists))
			.andExpect(status().isUnauthorized());
		
		// This user is not admin
		mockMvc.perform(get("/appclient/users/username").with(httpBasicHeader))
			.andExpect(status().isForbidden());
		
		// This user has admin permissions
		mockMvc.perform(get("/appclient/users/username").with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(3)))
			.andExpect(jsonPath("$[0]", is(u1.getUsername())))
			.andExpect(jsonPath("$[1]", is(u2.getUsername())))
			.andExpect(jsonPath("$[2]", is(u4.getUsername())));
	}
	
}
