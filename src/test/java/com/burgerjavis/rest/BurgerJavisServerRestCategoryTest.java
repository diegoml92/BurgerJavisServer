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
import com.burgerjavis.entities.User;
import com.burgerjavis.repositories.CategoryRepository;
import com.burgerjavis.repositories.UserRepository;
import com.burgerjavis.util.UnitTestUtil;
import com.burgerjavis.util.UnitTestUtil.UserRole;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={MongoTestConfiguration.class})
@WebAppConfiguration
public class BurgerJavisServerRestCategoryTest {
	
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private CategoryRepository categoryRepository;
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
	
	// CATEGORY HANDLER
	
	@Test
	public void testGetCategories() throws Exception {
		
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
		
		mockMvc.perform(get("/appclient/categories").with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(0)));
		mockMvc.perform(get("/appclient/categories").with(httpBasicHeader))
			.andExpect(status().isForbidden());
		mockMvc.perform(get("/appclient/categories").with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		mockMvc.perform(get("/appclient/categories").with(wrongHeader))
			.andExpect(status().isUnauthorized());
		// End Authentication-Authorization
		
		//Initialize database
		Category c1 = new Category("Burgers", "burger", false);
		Category c2 = new Category("Sandwiches", "sandwich", true);
		c1 = categoryRepository.save(c1);
		c2 = categoryRepository.save(c2);
		
		// Get category list
		mockMvc.perform(get("/appclient/categories").with(httpBasicHeaderAdmin))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
	    	.andExpect(jsonPath("$", hasSize(2)))
	    	.andExpect(jsonPath("$[0]._id", is(c1.get_id())))
	    	.andExpect(jsonPath("$[0].name", is(c1.getName())))
	    	.andExpect(jsonPath("$[0].icon", is(c1.getIcon())))
	    	.andExpect(jsonPath("$[0].favorite", is(c1.isFavorite())))
			.andExpect(jsonPath("$[1]._id", is(c2.get_id())))
	    	.andExpect(jsonPath("$[1].name", is(c2.getName())))
	    	.andExpect(jsonPath("$[1].icon", is(c2.getIcon())))
	    	.andExpect(jsonPath("$[1].favorite", is(c2.isFavorite())));
		
	}

	@Test
	public void testGetCategory() throws Exception {
		
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
		
		mockMvc.perform(get("/appclient/categories/thisIdDoesNotExist").with(httpBasicHeaderAdmin))
			.andExpect(status().isNotFound());
		mockMvc.perform(get("/appclient/categories/thisIdDoesNotExist").with(httpBasicHeader))
			.andExpect(status().isForbidden());
		mockMvc.perform(get("/appclient/categories/thisIdDoesNotExist").with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		mockMvc.perform(get("/appclient/categories/thisIdDoesNotExist").with(wrongHeader))
			.andExpect(status().isUnauthorized());
		// End Authentication-Authorization
		
		// Initialize database
		Category c1 = new Category("Burgers", "burger", false);
		Category c2 = new Category("Sandwiches", "sandwich", true);
		c1 = categoryRepository.save(c1);
		c2 = categoryRepository.save(c2);
		
		// Category with given id is returned
		mockMvc.perform(get("/appclient/categories/" + c2.get_id()).with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$._id", is(c2.get_id())))
			.andExpect(jsonPath("$.name", is(c2.getName())))
			.andExpect(jsonPath("$.icon", is(c2.getIcon())))
			.andExpect(jsonPath("$.favorite", is(c2.isFavorite())));
		
	}

	@Test
	public void testModifyCategory() throws Exception {
		
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
		
		mockMvc.perform(put("/appclient/categories/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Category()))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotFound());
		
		mockMvc.perform(put("/appclient/categories/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Category()))
					.with(httpBasicHeader))
			.andExpect(status().isForbidden());
		
		mockMvc.perform(put("/appclient/categories/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Category()))
					.with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		
		mockMvc.perform(put("/appclient/categories/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Category()))
					.with(wrongHeader))
			.andExpect(status().isUnauthorized());
		// End Authentication-Authorization
		
		
		//Initialize database
		Category c1 = new Category("Burgers", "burger", false);
		Category c2 = new Category("Sandwiches", "sandwich", true);
		c1 = categoryRepository.save(c1);
		c2 = categoryRepository.save(c2);
		
		// Modify c1
		Category modifiedCategory1 = new Category(c1);
		modifiedCategory1.setIcon("otherIcon");
		modifiedCategory1.setFavorite(true);
		
		// There is no category with such id
		mockMvc.perform(put("/appclient/categories/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedCategory1))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotFound());
		
		// Category is correctly modified
		mockMvc.perform(put("/appclient/categories/" + c1.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedCategory1))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._id", is(c1.get_id())))
			.andExpect(jsonPath("$.name", is(modifiedCategory1.getName())))
			.andExpect(jsonPath("$.icon", is(modifiedCategory1.getIcon())))
			.andExpect(jsonPath("$.favorite", is(modifiedCategory1.isFavorite())));
		
		// Modify p2
		Category modifiedCategory2 = new Category(c2);
		modifiedCategory2.setName("Super burgers");
		
		// Category with given id is correctly modified
		mockMvc.perform(put("/appclient/categories/" + c2.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedCategory2))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._id", is(c2.get_id())))
			.andExpect(jsonPath("$.name", is(modifiedCategory2.getName())))
			.andExpect(jsonPath("$.icon", is(modifiedCategory2.getIcon())))
			.andExpect(jsonPath("$.favorite", is(modifiedCategory2.isFavorite())));
		
		// Obtain category list and check categories are correctly modified
		mockMvc.perform(get("/appclient/categories").with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2)));
		
		// Create new categories
		Category c3 = new Category("Drinks", "beverage", true);
		Category c4 = new Category("Coffees", "coffee", false);
		c3 = categoryRepository.save(c3);
		c4 = categoryRepository.save(c4);
		
		Category modifiedCategory3 = new Category(c3);
		modifiedCategory3.setName("coffees");
		
		// The new name is already being used, must be rejected
		mockMvc.perform(put("/appclient/categories/" + c3.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedCategory3))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotAcceptable());
		
		// Obtain category list and check categories are correctly modified
		mockMvc.perform(get("/appclient/categories").with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(4)));
		
		Category modifiedCategory4 = new Category(c4);
		modifiedCategory4.setFavorite(true);
		
		// Maximum number of favorite categories reached
		mockMvc.perform(put("/appclient/categories/" + c4.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedCategory4))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotAcceptable());
		
	}

	@Test
	public void testDeleteCategory() throws Exception {
		
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
		
		mockMvc.perform(delete("/appclient/categories/thisIdDoesNotExist").with(wrongHeader))
			.andExpect(status().isUnauthorized());
		mockMvc.perform(delete("/appclient/categories/thisIdDoesNotExist").with(httpBasicHeader))
			.andExpect(status().isForbidden());
		mockMvc.perform(delete("/appclient/categories/thisIdDoesNotExist").with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		mockMvc.perform(delete("/appclient/categories/thisIdDoesNotExist").with(httpBasicHeaderAdmin))
			.andExpect(status().isNotFound());
		// End Authentication-Authorization
		
		// Initialize database
		Category c1 = new Category("Burgers", "burger", false);
		Category c2 = new Category("Sandwiches", "sandwich", true);
		c1 = categoryRepository.save(c1);
		c2 = categoryRepository.save(c2);
		
		// Category with given id is correctly deleted
		mockMvc.perform(delete("/appclient/categories/" + c1.get_id()).with(httpBasicHeaderAdmin))
				.andExpect(status().isOk());
		
		// Obtain category list and check that category was correctly deleted
		mockMvc.perform(get("/appclient/categories").with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0]._id", is(c2.get_id())))
			.andExpect(jsonPath("$[0].name", is(c2.getName())))
			.andExpect(jsonPath("$[0].icon", is(c2.getIcon())))
			.andExpect(jsonPath("$[0].favorite", is(c2.isFavorite())));
	}

	@Test
	public void testAddCategory() throws Exception {
		
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
		
		mockMvc.perform(post("/appclient/categories")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Category()))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotAcceptable());
		
		mockMvc.perform(post("/appclient/categories")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Category()))
					.with(httpBasicHeader))
			.andExpect(status().isForbidden());
		
		mockMvc.perform(post("/appclient/categories")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Category()))
					.with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		
		mockMvc.perform(post("/appclient/categories")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Category()))
					.with(wrongHeader))
			.andExpect(status().isUnauthorized());
		// End Authentication-Authorization
		
		//Initialize database
		Category c1 = new Category("Burgers", "burger", true);
		Category c2 = new Category("Sandwiches", "sandwich", true);
		c1 = categoryRepository.save(c1);
		c2 = categoryRepository.save(c2);
		
		// A category with this name already exists
		Category c3 = new Category("burgers", "drink", false);
		mockMvc.perform(post("/appclient/categories")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(c3))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotAcceptable());
		
		// This category is correctly created
		Category c4 = new Category("Pizzas", "pizza", true);
		mockMvc.perform(post("/appclient/categories")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(c4))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name", is(c4.getName())))
			.andExpect(jsonPath("$.icon", is(c4.getIcon())))
			.andExpect(jsonPath("$.favorite", is(c4.isFavorite())));
		
		// Obtain category list and check categories were correctyl added
		mockMvc.perform(get("/appclient/categories").with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(3)));
	}
	
}
