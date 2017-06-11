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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.burgerjavis.Common.OrderState;
import com.burgerjavis.MongoTestConfiguration;
import com.burgerjavis.entities.Order;
import com.burgerjavis.entities.OrderItem;
import com.burgerjavis.entities.User;
import com.burgerjavis.repositories.OrderRepository;
import com.burgerjavis.repositories.UserRepository;
import com.burgerjavis.util.UnitTestUtil;
import com.burgerjavis.util.UnitTestUtil.UserRole;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={MongoTestConfiguration.class})
@WebAppConfiguration
public class BurgerJavisServerRestKitchenTest {
	
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private OrderRepository orderRepository;
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
	public void testGetKitchenOrders() throws Exception {
		
		// Authentication-Authorization
		final String USERNAME = "user1";
		final String PASSWORD = "pass";
		final String USERNAME2 = "admin";
		final String PASSWORD2 = "admin";
		final String USERNAME3 = "user2";
		final String PASSWORD3 = "pass";
		User user1 = UnitTestUtil.generateUser (USERNAME, PASSWORD, UserRole.ROLE_WAITER);
		User admin = UnitTestUtil.generateUser (USERNAME2, PASSWORD2, UserRole.ROLE_ADMIN);
		User user2 = UnitTestUtil.generateUser(USERNAME3, PASSWORD3, UserRole.ROLE_KITCHEN);
		userRepository.save(user1);
		userRepository.save(admin);
		userRepository.save(user2);
		
		RequestPostProcessor httpBasicHeader = httpBasic(USERNAME, PASSWORD);
		RequestPostProcessor httpBasicHeaderAdmin = httpBasic(USERNAME2, PASSWORD2);
		RequestPostProcessor httpBasicHeaderUser2 = httpBasic(USERNAME3, PASSWORD3);
		RequestPostProcessor wrongHeader = httpBasic(USERNAME, PASSWORD2);
		
		mockMvc.perform(get("/appclient/kitchen").with(wrongHeader))
			.andExpect(status().isUnauthorized());
		
		mockMvc.perform(get("/appclient/kitchen").with(httpBasicHeader))
		.andExpect(status().isForbidden());
		
		mockMvc.perform(get("/appclient/kitchen").with(httpBasicHeaderAdmin))
		.andExpect(status().isOk())
		.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$", hasSize(0)));
		
		mockMvc.perform(get("/appclient/kitchen").with(httpBasicHeaderUser2))
    		.andExpect(status().isOk())
    		.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
    		.andExpect(jsonPath("$", hasSize(0)));
		// End Authentication-Authorization
		
		//Initialize database
		Order order1 = new Order("Order 1", new ArrayList<OrderItem>(), OrderState.INITIAL, USERNAME);
		Order order2 = new Order("Order 2", new ArrayList<OrderItem>(), OrderState.KITCHEN, USERNAME);
		Order order3 = new Order("Order 3", new ArrayList<OrderItem>(), OrderState.FINISHED, USERNAME2);
		Order order4 = new Order("Order 4", new ArrayList<OrderItem>(), OrderState.SERVED, USERNAME2);
		Order order5 = new Order("Order 5", new ArrayList<OrderItem>(), OrderState.KITCHEN, USERNAME2);
		orderRepository.save(order1);
		orderRepository.save(order2);
		orderRepository.save(order3);
		orderRepository.save(order4);
		orderRepository.save(order5);
		
		// Only orders in KITCHEN state are returned
		mockMvc.perform(get("/appclient/kitchen").with(httpBasicHeaderUser2))
        	.andExpect(status().isOk())
        	.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
        	.andExpect(jsonPath("$", hasSize(2)))
        	.andExpect(jsonPath("$[0]._id", is(order2.get_id())))
        	.andExpect(jsonPath("$[0].name", is(order2.getName())))
        	.andExpect(jsonPath("$[0].items", hasSize(order2.getItems().size())))
        	.andExpect(jsonPath("$[0].state", is(order2.getState().name())))
        	.andExpect(jsonPath("$[0].username",  is(order2.getUsername())))
        	.andExpect(jsonPath("$[1]._id", is(order5.get_id())))
        	.andExpect(jsonPath("$[1].name", is(order5.getName())))
        	.andExpect(jsonPath("$[1].items", hasSize(order5.getItems().size())))
        	.andExpect(jsonPath("$[1].state", is(order5.getState().name())))
        	.andExpect(jsonPath("$[1].username",  is(order5.getUsername())));
	
	}
	
	@Test
	public void testGetKitchen() throws Exception {
		
		// Authentication-Authorization
		final String USERNAME = "user1";
		final String PASSWORD = "pass";
		final String USERNAME2 = "admin";
		final String PASSWORD2 = "admin";
		final String USERNAME3 = "user2";
		final String PASSWORD3 = "pass";
		User user1 = UnitTestUtil.generateUser (USERNAME, PASSWORD, UserRole.ROLE_WAITER);
		User admin = UnitTestUtil.generateUser (USERNAME2, PASSWORD2, UserRole.ROLE_ADMIN);
		User user2 = UnitTestUtil.generateUser(USERNAME3, PASSWORD3, UserRole.ROLE_KITCHEN);
		userRepository.save(user1);
		userRepository.save(admin);
		userRepository.save(user2);
		
		RequestPostProcessor httpBasicHeader = httpBasic(USERNAME, PASSWORD);
		RequestPostProcessor httpBasicHeaderAdmin = httpBasic(USERNAME2, PASSWORD2);
		RequestPostProcessor httpBasicHeaderUser2 = httpBasic(USERNAME3, PASSWORD3);
		RequestPostProcessor wrongHeader = httpBasic(USERNAME, PASSWORD2);
		
		mockMvc.perform(get("/appclient/kitchen/thisIdDoesNotExist").with(httpBasicHeaderUser2))
			.andExpect(status().isNotFound());
		mockMvc.perform(get("/appclient/kitchen/thisIdDoesNotExist").with(httpBasicHeaderAdmin))
			.andExpect(status().isNotFound());
		mockMvc.perform(get("/appclient/kitchen/thisIdDoesNotExist").with(wrongHeader))
			.andExpect(status().isUnauthorized());
		mockMvc.perform(get("/appclient/kitchen/thisIdDoesNotExist").with(httpBasicHeader))
			.andExpect(status().isForbidden());
		// End Authentication-Authorization
		
		//Initialize database
		Order order1 = new Order("Order 1", USERNAME);
		Order order2 = new Order("Order 2", new ArrayList<OrderItem>(), OrderState.KITCHEN, USERNAME2);
		order1 = orderRepository.save(order1);
		order2 = orderRepository.save(order2);
		
		// Order with given id exists and is in KITCHEN state
		mockMvc.perform(get("/appclient/kitchen/" + order2.get_id()).with(httpBasicHeaderUser2))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$._id", is(order2.get_id())))
			.andExpect(jsonPath("$.name", is(order2.getName())))
			.andExpect(jsonPath("$.items", hasSize(order2.getItems().size())))
			.andExpect(jsonPath("$.state", is(order2.getState().name())))
			.andExpect(jsonPath("$.username",  is(order2.getUsername())));
		
		// Order is not in KITCHEN state
		mockMvc.perform(get("/appclient/kitchen/" + order1.get_id()).with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());

	}
	
	@Test
	public void testModifyKitchen() throws Exception {
		
		// Authentication-Authorization
		final String USERNAME = "user1";
		final String PASSWORD = "pass";
		final String USERNAME2 = "admin";
		final String PASSWORD2 = "admin";
		final String USERNAME3 = "user2";
		final String PASSWORD3 = "pass";
		User user1 = UnitTestUtil.generateUser (USERNAME, PASSWORD, UserRole.ROLE_WAITER);
		User admin = UnitTestUtil.generateUser (USERNAME2, PASSWORD2, UserRole.ROLE_ADMIN);
		User user2 = UnitTestUtil.generateUser(USERNAME3, PASSWORD3, UserRole.ROLE_KITCHEN);
		userRepository.save(user1);
		userRepository.save(admin);
		userRepository.save(user2);
		
		RequestPostProcessor httpBasicHeader = httpBasic(USERNAME, PASSWORD);
		RequestPostProcessor httpBasicHeaderAdmin = httpBasic(USERNAME2, PASSWORD2);
		RequestPostProcessor httpBasicHeaderUser2 = httpBasic(USERNAME3, PASSWORD3);
		RequestPostProcessor wrongHeader = httpBasic(USERNAME, PASSWORD2);
		
		mockMvc.perform(put("/appclient/kitchen/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Order()))
					.with(httpBasicHeaderUser2))
			.andExpect(status().isNotFound());
		
		mockMvc.perform(put("/appclient/kitchen/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Order()))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotFound());
		
		mockMvc.perform(put("/appclient/kitchen/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Order()))
					.with(wrongHeader))
			.andExpect(status().isUnauthorized());
		
		mockMvc.perform(put("/appclient/kitchen/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Order()))
					.with(httpBasicHeader))
			.andExpect(status().isForbidden());
		
		//Initialize database
		Order order1 = new Order("Order 1", USERNAME);
		Order order2 = new Order("Order 2", new ArrayList<OrderItem>(), OrderState.KITCHEN, USERNAME2);
		order1 = orderRepository.save(order1);
		order2 = orderRepository.save(order2);
		
		// Order with given id exists but is not in KITCHEN state
		mockMvc.perform(put("/appclient/kitchen/" + order1.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(order1))
					.with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		
		// Order with given id exists and is in KITCHEN state
		mockMvc.perform(put("/appclient/kitchen/" + order2.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(order2))
					.with(httpBasicHeaderUser2))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._id", is(order2.get_id())))
			.andExpect(jsonPath("$.name", is(order2.getName())))
			.andExpect(jsonPath("$.items", hasSize(order2.getItems().size())))
			.andExpect(jsonPath("$.state", is(OrderState.SERVED.name())))
			.andExpect(jsonPath("$.username",  is(order2.getUsername())));
	}
	
}
