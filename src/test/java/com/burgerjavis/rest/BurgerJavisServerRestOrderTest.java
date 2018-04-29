/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class BurgerJavisServerRestOrderTest {
	
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
	
	// ORDER HANDLER

	@Test
	public void testGetOrders() throws Exception {
		
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
		
		mockMvc.perform(get("/appclient/orders").with(wrongHeader))
			.andExpect(status().isUnauthorized());
		
		mockMvc.perform(get("/appclient/orders").with(httpBasicHeaderUser2))
		.andExpect(status().isForbidden());
		
		mockMvc.perform(get("/appclient/orders").with(httpBasicHeaderAdmin))
		.andExpect(status().isOk())
		.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$", hasSize(0)));
		
		mockMvc.perform(get("/appclient/orders").with(httpBasicHeader))
    		.andExpect(status().isOk())
    		.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
    		.andExpect(jsonPath("$", hasSize(0)));
		// End Authentication-Authorization
		
		//Initialize database
		Order order1 = new Order("Order 1", USERNAME);
		Order order2 = new Order("Order 2", new ArrayList<OrderItem>(), OrderState.FINISHED, USERNAME);
		Order order3 = new Order("Order 3", USERNAME2);
		Order order4 = new Order("Order 4", new ArrayList<OrderItem>(), OrderState.FINISHED, USERNAME2);
		orderRepository.save(order1);
		orderRepository.save(order2);
		orderRepository.save(order3);
		orderRepository.save(order4);
		
		// Only orders belonging to "user1" that are not finished are returned
		mockMvc.perform(get("/appclient/orders").with(httpBasicHeader))
        	.andExpect(status().isOk())
        	.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
        	.andExpect(jsonPath("$", hasSize(1)))
        	.andExpect(jsonPath("$[0]._id", is(order1.get_id())))
        	.andExpect(jsonPath("$[0].name", is(order1.getName())))
        	.andExpect(jsonPath("$[0].items", hasSize(order1.getItems().size())))
        	.andExpect(jsonPath("$[0].state", is(order1.getState().name())))
        	.andExpect(jsonPath("$[0].username",  is(order1.getUsername())));
		
		// All orders that are not finished are returned
		mockMvc.perform(get("/appclient/orders").with(httpBasicHeaderAdmin))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
	    	.andExpect(jsonPath("$", hasSize(2)))
	    	.andExpect(jsonPath("$[0]._id", is(order1.get_id())))
	    	.andExpect(jsonPath("$[0].name", is(order1.getName())))
	    	.andExpect(jsonPath("$[0].items", hasSize(order1.getItems().size())))
	    	.andExpect(jsonPath("$[0].state", is(order1.getState().name())))
	    	.andExpect(jsonPath("$[0].username",  is(order1.getUsername())))
	    	.andExpect(jsonPath("$[1]._id", is(order3.get_id())))
	    	.andExpect(jsonPath("$[1].name", is(order3.getName())))
	    	.andExpect(jsonPath("$[1].items", hasSize(order3.getItems().size())))
	    	.andExpect(jsonPath("$[1].state", is(order3.getState().name())))
	    	.andExpect(jsonPath("$[1].username",  is(order3.getUsername())));

	}

	@Test
	public void testGetOrder() throws Exception {
		
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
		
		mockMvc.perform(get("/appclient/orders/thisIdDoesNotExist").with(httpBasicHeader))
			.andExpect(status().isNotFound());
		mockMvc.perform(get("/appclient/orders/thisIdDoesNotExist").with(httpBasicHeaderAdmin))
			.andExpect(status().isNotFound());
		mockMvc.perform(get("/appclient/orders/thisIdDoesNotExist").with(wrongHeader))
			.andExpect(status().isUnauthorized());
		mockMvc.perform(get("/appclient/orders/thisIdDoesNotExist").with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		// End Authentication-Authorization
		
		//Initialize database
		Order order1 = new Order("Order 1", USERNAME);
		Order order2 = new Order("Order 2", new ArrayList<OrderItem>(), OrderState.FINISHED, USERNAME);
		Order order3 = new Order("Order 3", USERNAME2);
		order1 = orderRepository.save(order1);
		order2 = orderRepository.save(order2);
		order3 = orderRepository.save(order3);
		
		// Order with given id exists and belongs to "user1"
		mockMvc.perform(get("/appclient/orders/" + order1.get_id()).with(httpBasicHeader))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$._id", is(order1.get_id())))
			.andExpect(jsonPath("$.name", is(order1.getName())))
			.andExpect(jsonPath("$.items", hasSize(order1.getItems().size())))
			.andExpect(jsonPath("$.state", is(order1.getState().name())))
			.andExpect(jsonPath("$.username",  is(order1.getUsername())));
		
		// Order belongs to "user1" but is already finished
		mockMvc.perform(get("/appclient/orders/" + order2.get_id()).with(httpBasicHeader))
			.andExpect(status().isForbidden());
		
		// Order belongs to "admin" so "user1" can't access to it
		mockMvc.perform(get("/appclient/orders/" + order3.get_id()).with(httpBasicHeader))
			.andExpect(status().isUnauthorized());

	}

	@Test
	public void testModifyOrder() throws Exception {
		
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
		
		mockMvc.perform(put("/appclient/orders/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Order()))
					.with(httpBasicHeader))
			.andExpect(status().isNotFound());
		
		mockMvc.perform(put("/appclient/orders/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Order()))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotFound());
		
		mockMvc.perform(put("/appclient/orders/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Order()))
					.with(wrongHeader))
			.andExpect(status().isUnauthorized());
		
		mockMvc.perform(put("/appclient/orders/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Order()))
					.with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		// End Authentication-Authorization
		
		//Initialize database
		Order order1 = new Order("Order 1", USERNAME);
		Order order2 = new Order("Order 2", new ArrayList<OrderItem>(), OrderState.FINISHED, USERNAME2);
		order1 = orderRepository.save(order1);
		order2 = orderRepository.save(order2);
		
		// Modify order1
		Order modifiedOrder1 = new Order(order1);
		modifiedOrder1.setName("New order");
		modifiedOrder1.setState(OrderState.FINISHED);
		
		// Order id does not exist
		mockMvc.perform(put("/appclient/orders/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedOrder1))
					.with(httpBasicHeader))
			.andExpect(status().isNotFound());
		
		// Order with given id exists and belongs to "user1"
		mockMvc.perform(put("/appclient/orders/" + order1.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedOrder1))
					.with(httpBasicHeader))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._id", is(order1.get_id())))
			.andExpect(jsonPath("$.name", is(modifiedOrder1.getName())))
			.andExpect(jsonPath("$.items", hasSize(modifiedOrder1.getItems().size())))
			.andExpect(jsonPath("$.state", is(modifiedOrder1.getState().name())))
			.andExpect(jsonPath("$.username",  is(modifiedOrder1.getUsername())));
		
		// None of the orders belonging to "user1" are active
		mockMvc.perform(get("/appclient/orders").with(httpBasicHeader))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(0)));
		
		// Create new orders
		Order order3 = new Order("Order 3", new ArrayList<OrderItem>(), OrderState.FINISHED, USERNAME);
		Order order4 = new Order("Order 4", USERNAME2);
		Order order5 = new Order("Order 5", USERNAME);
		order3 = orderRepository.save(order3);
		order4 = orderRepository.save(order4);
		order5 = orderRepository.save(order5);
		
		Order modifiedOrder2 = new Order(order5);
		modifiedOrder2.setName("order 4");
		
		// The new name is already being used, should be rejected
		mockMvc.perform(put("/appclient/orders/" + order5.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedOrder2))
					.with(httpBasicHeader))
			.andExpect(status().isNotAcceptable());
		
		// The order is already finished, should be rejected
		mockMvc.perform(put("/appclient/orders/" + order3.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedOrder2))
					.with(httpBasicHeader))
			.andExpect(status().isForbidden());
		
		Order modifiedOrder3 = new Order(order4);
		modifiedOrder3.setName("Invalid-Name?");
		
		// This order belongs to "admin" so "user1" has no access to it
		mockMvc.perform(put("/appclient/orders/" + order4.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedOrder3))
					.with(httpBasicHeader))
			.andExpect(status().isUnauthorized());
		
		// The new name contains invalid characters
		mockMvc.perform(put("/appclient/orders/" + order4.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedOrder3))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotAcceptable());
		
		// Create new orders
		Order order6 = new Order("Order 6", new ArrayList<OrderItem>(), OrderState.KITCHEN, USERNAME);
		Order order7 = new Order("Order 7", new ArrayList<OrderItem>(), OrderState.SERVED, USERNAME2);
		order3 = orderRepository.save(order6);
		order4 = orderRepository.save(order7);
		
		Order modifiedOrder4 = new Order(order6);
		modifiedOrder4.setName("Modified order 4");
		
		// Order with given id exists and belongs to "user1", but its state is invalid
		mockMvc.perform(put("/appclient/orders/" + order6.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedOrder1))
					.with(httpBasicHeader))
			.andExpect(status().isForbidden());
		
		Order modifiedOrder5 = new Order(order7);
		modifiedOrder5.setName("Modified order 5");
		
		// Order with given id exists and belongs to "admin"
		mockMvc.perform(put("/appclient/orders/" + order7.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedOrder1))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._id", is(order7.get_id())))
			.andExpect(jsonPath("$.name", is(order7.getName())))
			.andExpect(jsonPath("$.items", hasSize(order7.getItems().size())))
			.andExpect(jsonPath("$.state", is(OrderState.FINISHED.name())))
			.andExpect(jsonPath("$.username",  is(order7.getUsername())));
		
		
		// Only orders belonging to "user1" that are not finished are returned
		mockMvc.perform(get("/appclient/orders").with(httpBasicHeader))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2)));
	
	}

	@Test
	public void testDeleteOrder() throws Exception {
		
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
		
		mockMvc.perform(delete("/appclient/orders/thisIdDoesNotExist").with(httpBasicHeader))
			.andExpect(status().isNotFound());
		mockMvc.perform(delete("/appclient/orders/thisIdDoesNotExist").with(httpBasicHeaderAdmin))
			.andExpect(status().isNotFound());
		mockMvc.perform(delete("/appclient/orders/thisIdDoesNotExist").with(wrongHeader))
			.andExpect(status().isUnauthorized());
		mockMvc.perform(delete("/appclient/orders/thisIdDoesNotExist").with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		// End Authentication-Authorization
		
		//Initialize database
		Order order1 = new Order("Order 1", USERNAME);
		Order order2 = new Order("Order 2", new ArrayList<OrderItem>(), OrderState.FINISHED, USERNAME);
		Order order3 = new Order("Order 3", USERNAME2);
		order1 = orderRepository.save(order1);
		order2 = orderRepository.save(order2);
		order3 = orderRepository.save(order3);
		
		// Order with given id belongs to "user1" and is not finished
		mockMvc.perform(delete("/appclient/orders/" + order1.get_id()).with(httpBasicHeader))
			.andExpect(status().isOk());
		
		// Order with given id belongs to "user1" but it is finished
		mockMvc.perform(delete("/appclient/orders/" + order2.get_id()).with(httpBasicHeader))
			.andExpect(status().isForbidden());
		
		// Order with given id belongs to "admin" so "user1" can't delete it
		mockMvc.perform(delete("/appclient/orders/" + order3.get_id()).with(httpBasicHeader))
			.andExpect(status().isUnauthorized());
		
		// Only orders belonging to "user1" that are not finished are returned
		mockMvc.perform(get("/appclient/orders").with(httpBasicHeader))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(0)));
				
	}

	@Test
	public void testAddOrder() throws Exception {
		
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
		
		mockMvc.perform(post("/appclient/orders/")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Order()))
					.with(httpBasicHeader))
			.andExpect(status().isNotAcceptable());
		mockMvc.perform(post("/appclient/orders/")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Order()))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotAcceptable());
		mockMvc.perform(post("/appclient/orders/")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Order()))
					.with(wrongHeader))
			.andExpect(status().isUnauthorized());
		mockMvc.perform(post("/appclient/orders/")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Order()))
					.with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		// End Authentication-Authorization
		
		//Initialize database
		Order order1 = new Order("Order 1", USERNAME);
		Order order2 = new Order("Order 2", new ArrayList<OrderItem>(), OrderState.FINISHED, USERNAME2);
		order1 = orderRepository.save(order1);
		order2 = orderRepository.save(order2);
		
		// An order with this name already exists, and is not finished
		Order order3 = new Order("order 1", USERNAME2);
		mockMvc.perform(post("/appclient/orders/")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(order3))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotAcceptable());
		
		// An order with this name already exists, but it is finished
		Order order4 = new Order("Order 2", USERNAME);
		mockMvc.perform(post("/appclient/orders/")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(order4))
					.with(httpBasicHeader))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.name", is(order4.getName())))
			.andExpect(jsonPath("$.items", hasSize(order4.getItems().size())))
			.andExpect(jsonPath("$.state", is(order4.getState().name())))
			.andExpect(jsonPath("$.username",  is(order4.getUsername())));
		
		// The order name contains invalid characters
		Order order5 = new Order("Order 5??", USERNAME);
		mockMvc.perform(post("/appclient/orders/")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(order5))
					.with(httpBasicHeader))
			.andExpect(status().isNotAcceptable());
		
		// An order with this name already exists, but it is finished
		Order order6 = new Order("Order 6", new ArrayList<OrderItem>(), OrderState.FINISHED, USERNAME);
		mockMvc.perform(post("/appclient/orders/")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(order6))
					.with(httpBasicHeader))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.name", is(order6.getName())))
			.andExpect(jsonPath("$.items", hasSize(order6.getItems().size())))
			.andExpect(jsonPath("$.state", is(order6.getState().name())))
			.andExpect(jsonPath("$.username",  is(order6.getUsername())));

	}

}
