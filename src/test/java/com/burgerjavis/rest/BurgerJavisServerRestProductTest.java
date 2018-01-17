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

import com.burgerjavis.MongoTestConfiguration;
import com.burgerjavis.entities.Category;
import com.burgerjavis.entities.Product;
import com.burgerjavis.entities.User;
import com.burgerjavis.repositories.ProductRepository;
import com.burgerjavis.repositories.UserRepository;
import com.burgerjavis.util.UnitTestUtil;
import com.burgerjavis.util.UnitTestUtil.UserRole;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={MongoTestConfiguration.class})
@WebAppConfiguration
public class BurgerJavisServerRestProductTest {
	
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private ProductRepository productRepository;
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
	
	// PRODUCT HANDLER

	@Test
	public void testGetProducts() throws Exception {
		
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
		
		mockMvc.perform(get("/appclient/products").with(httpBasicHeader))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(0)));
		
		mockMvc.perform(get("/appclient/products").with(httpBasicHeaderUser2))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(0)));
		
		mockMvc.perform(get("/appclient/products").with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(0)));
		
		mockMvc.perform(get("/appclient/products").with(wrongHeader))
			.andExpect(status().isUnauthorized());
		// End Authentication-Authorization
		
		//Initialize database
		Product p1 = new Product("Burger", 4.5f);
		Category category = new Category("Sandwiches", "sandwich");
		Product p2 = new Product("Sandwich", 5.0f, category);
		productRepository.save(p1);
		productRepository.save(p2);
		
		// List of products is returned
		mockMvc.perform(get("/appclient/products").with(httpBasicHeader))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
	    	.andExpect(jsonPath("$", hasSize(2)))
	    	.andExpect(jsonPath("$[0]._id", is(p1.get_id())))
	    	.andExpect(jsonPath("$[0].name", is(p1.getName())))
	    	.andExpect(jsonPath("$[0].price", is((double)p1.getPrice())))
	    	.andExpect(jsonPath("$[0].ingredients", hasSize(p1.getIngredients().size())))
	    	.andExpect(jsonPath("$[1]._id", is(p2.get_id())))
	    	.andExpect(jsonPath("$[1].name", is(p2.getName())))
	    	.andExpect(jsonPath("$[1].price", is((double)p2.getPrice())))
	    	.andExpect(jsonPath("$[1].ingredients", hasSize(p2.getIngredients().size())));
	}

	@Test
	public void testGetProduct() throws Exception {
		
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
		
		mockMvc.perform(get("/appclient/products/thisIdDoesNotExist").with(httpBasicHeaderAdmin))
			.andExpect(status().isNotFound());
		mockMvc.perform(get("/appclient/products/thisIdDoesNotExist").with(httpBasicHeader))
			.andExpect(status().isForbidden());
		mockMvc.perform(get("/appclient/products/thisIdDoesNotExist").with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		mockMvc.perform(get("/appclient/products/thisIdDoesNotExist").with(wrongHeader))
			.andExpect(status().isUnauthorized());
		// End Authentication-Authorization
		
		// Initialize database
		Product p1 = new Product("Burger", 4.5f);
		Category category = new Category("Sandwiches", "sandwich");
		Product p2 = new Product("Sandwich", 5.0f, category);
		productRepository.save(p1);
		productRepository.save(p2);
		
		// Product with given id is returned
		mockMvc.perform(get("/appclient/products/" + p2.get_id()).with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$._id", is(p2.get_id())))
			.andExpect(jsonPath("$.name", is(p2.getName())))
			.andExpect(jsonPath("$.ingredients", hasSize(p2.getIngredients().size())));
		
	}

	@Test
	public void testModifyProduct() throws Exception {
		
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
		
		mockMvc.perform(put("/appclient/products/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Product()))
					.with(wrongHeader))
			.andExpect(status().isUnauthorized());
		
		mockMvc.perform(put("/appclient/products/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Product()))
					.with(httpBasicHeader))
			.andExpect(status().isForbidden());
		
		mockMvc.perform(put("/appclient/products/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Product()))
					.with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		
		mockMvc.perform(put("/appclient/products/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Product()))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotFound());
		// End Authentication-Authorization
		
		// Initialize database
		Product p1 = new Product("Burger", 4.5f);
		Category category = new Category("Sandwiches", "sandwich");
		Product p2 = new Product("Sandwich", 5.0f, category);
		productRepository.save(p1);
		productRepository.save(p2);
		
		// Modify p2
		Product modifiedProduct1 = new Product(p2);
		modifiedProduct1.setName("MegaSandwich");
		
		// Product referenced by given id is modified
		mockMvc.perform(put("/appclient/products/" + p2.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedProduct1))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._id", is(p2.get_id())))
			.andExpect(jsonPath("$.name", is(modifiedProduct1.getName())))
			.andExpect(jsonPath("$.price", is((double)modifiedProduct1.getPrice())))
			.andExpect(jsonPath("$.category.name", is(modifiedProduct1.getCategory().getName())))
			.andExpect(jsonPath("$.category.icon", is(modifiedProduct1.getCategory().getIcon())))
			.andExpect(jsonPath("$.ingredients", hasSize(modifiedProduct1.getIngredients().size())));
		
		// Modify p1
		Product modifiedProduct2 = new Product(p1);
		modifiedProduct2.setCategory(new Category("Burgers", "burger", true));
		
		// Product referenced by given id is modified
		mockMvc.perform(put("/appclient/products/" + p1.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedProduct2))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._id", is(p1.get_id())))
			.andExpect(jsonPath("$.name", is(modifiedProduct2.getName())))
			.andExpect(jsonPath("$.price", is((double)modifiedProduct2.getPrice())))
			.andExpect(jsonPath("$.category.name", is(modifiedProduct2.getCategory().getName())))
			.andExpect(jsonPath("$.category.icon", is(modifiedProduct2.getCategory().getIcon())))
			.andExpect(jsonPath("$.ingredients", hasSize(modifiedProduct2.getIngredients().size())));
		
		// Product list is obtained
		mockMvc.perform(get("/appclient/products").with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2)));
		
		// Create new products
		Category category2 = new Category("Drinks", "drink", false);
		Product p3 = new Product("Coke", 2.20f, category2);
		Product p4 = new Product("Water", 1.5f, category2);
		p3 = productRepository.save(p3);
		p4 = productRepository.save(p4);
		
		Product modifiedProduct3 = new Product(p3);
		modifiedProduct3.setName("water");
		
		// The new name is already being used, should be rejected
		mockMvc.perform(put("/appclient/products/" + p3.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedProduct3))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotAcceptable());
		
		Product modifiedProduct4 = new Product(p4);
		modifiedProduct4.setPrice(-4.1f);
		
		// The new price is invalid
		mockMvc.perform(put("/appclient/products/" + p4.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedProduct4))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotAcceptable());
		
		// Product list is obtained
		mockMvc.perform(get("/appclient/products").with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(4)));
				
	}

	@Test
	public void testDeleteProduct() throws Exception {
		
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
		
		mockMvc.perform(delete("/appclient/products/thisIdDoesNotExist").with(httpBasicHeaderAdmin))
			.andExpect(status().isNotFound());
		mockMvc.perform(delete("/appclient/products/thisIdDoesNotExist").with(httpBasicHeader))
			.andExpect(status().isForbidden());
		mockMvc.perform(delete("/appclient/products/thisIdDoesNotExist").with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		mockMvc.perform(delete("/appclient/products/thisIdDoesNotExist").with(wrongHeader))
			.andExpect(status().isUnauthorized());
		// End Authentication-Authorization
		
		// Initialize database
		Product p1 = new Product("Burger", 4.5f);
		Category category = new Category("Sandwiches", "sandwich");
		Product p2 = new Product("Sandwich", 5.0f, category);
		productRepository.save(p1);
		productRepository.save(p2);
		
		// Delete product referenced by given id
		mockMvc.perform(delete("/appclient/products/" + p1.get_id()).with(httpBasicHeaderAdmin))
				.andExpect(status().isOk());
		
		// Obtain product list and check that the product was deleted
		mockMvc.perform(get("/appclient/products").with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1)));		
	}

	@Test
	public void testAddProduct() throws Exception {
		
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
		
		mockMvc.perform(post("/appclient/products")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Product()))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotAcceptable());
		mockMvc.perform(post("/appclient/products")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Product()))
					.with(httpBasicHeader))
			.andExpect(status().isForbidden());
		mockMvc.perform(post("/appclient/products")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Product()))
					.with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		mockMvc.perform(post("/appclient/products")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Product()))
					.with(wrongHeader))
			.andExpect(status().isUnauthorized());
		// Authentication-Authorization
		
		// Initialize database
		Product p1 = new Product("Burger", 4.5f);
		Category category = new Category("Sandwiches", "sandwich");
		Product p2 = new Product("Sandwich", 5.0f, category);
		productRepository.save(p1);
		productRepository.save(p2);
		
		// A product with this name already exists
		Product p3 = new Product("Burger", 5.6f);
		
		// Product name is already being used, should be rejected
		mockMvc.perform(post("/appclient/products")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(p3))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotAcceptable());
		
		// The product name contains invalid characters
		Product p4 = new Product("Burger??", 5.6f);
		mockMvc.perform(post("/appclient/products")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(p4))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotAcceptable());
		
		// The product is created
		Product p5 = new Product("Hotdog", 3.50f);
		mockMvc.perform(post("/appclient/products")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(p5))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name", is(p5.getName())))
			.andExpect(jsonPath("$.price", is((double)p5.getPrice())))
			.andExpect(jsonPath("$.category.name", is(p5.getCategory().getName())))
			.andExpect(jsonPath("$.category.icon", is(p5.getCategory().getIcon())))
			.andExpect(jsonPath("$.ingredients", hasSize(p5.getIngredients().size())));
		
		// Obtain the product lit and check that every product has been added
		mockMvc.perform(get("/appclient/products").with(httpBasicHeader))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(3)));
	}
	
}
