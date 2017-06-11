/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.rest;

import static org.hamcrest.Matchers.closeTo;
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
import com.burgerjavis.entities.Ingredient;
import com.burgerjavis.entities.User;
import com.burgerjavis.repositories.IngredientRepository;
import com.burgerjavis.repositories.UserRepository;
import com.burgerjavis.util.UnitTestUtil;
import com.burgerjavis.util.UnitTestUtil.UserRole;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={MongoTestConfiguration.class})
@WebAppConfiguration
public class BurgerJavisServerRestIngredientTest {
	
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private IngredientRepository ingredientRepository;
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
	
	// INGREDIENT HANDLER

	@Test
	public void testGetIngredients() throws Exception {
		
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
		
		mockMvc.perform(get("/appclient/ingredients").with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(0)));
		mockMvc.perform(get("/appclient/ingredients").with(httpBasicHeader))
			.andExpect(status().isForbidden());
		mockMvc.perform(get("/appclient/ingredients").with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		mockMvc.perform(get("/appclient/ingredients").with(wrongHeader))
			.andExpect(status().isUnauthorized());
		// End Authentication-Authorization
		
		//Initialize database
		Ingredient i1 = new Ingredient("Tomato");
		Ingredient i2 = new Ingredient("Cheese", 0.25f);
		ingredientRepository.save(i1);
		ingredientRepository.save(i2);
		
		// Ingredient list is returned
		mockMvc.perform(get("/appclient/ingredients").with(httpBasicHeaderAdmin))
	    	.andExpect(status().isOk())
	    	.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
	    	.andExpect(jsonPath("$", hasSize(2)))
	    	.andExpect(jsonPath("$[0]._id", is(i1.get_id())))
	    	.andExpect(jsonPath("$[0].name", is(i1.getName())))
	    	.andExpect(jsonPath("$[0].extraPrice", is((double)i1.getExtraPrice())))
	    	.andExpect(jsonPath("$[1]._id", is(i2.get_id())))
	    	.andExpect(jsonPath("$[1].name", is(i2.getName())))
	    	.andExpect(jsonPath("$[1].extraPrice", is((double)i2.getExtraPrice())));
	}

	@Test
	public void testGetIngredient() throws Exception {
		
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
		
		mockMvc.perform(get("/appclient/ingredients/thisIdDoesNotExist").with(httpBasicHeaderAdmin))
			.andExpect(status().isNotFound());
		mockMvc.perform(get("/appclient/ingredients/thisIdDoesNotExist").with(httpBasicHeader))
			.andExpect(status().isForbidden());
		mockMvc.perform(get("/appclient/ingredients/thisIdDoesNotExist").with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		mockMvc.perform(get("/appclient/ingredients/thisIdDoesNotExist").with(wrongHeader))
			.andExpect(status().isUnauthorized());
		// End Authentication-Authorization
		
		//Initialize database
		Ingredient i1 = new Ingredient("Tomato");
		Ingredient i2 = new Ingredient("Cheese", 0.25f);
		i1 = ingredientRepository.save(i1);
		i2 = ingredientRepository.save(i2);
		
		// Ingredient with given id is returned
		mockMvc.perform(get("/appclient/ingredients/" + i2.get_id()).with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$._id", is(i2.get_id())))
			.andExpect(jsonPath("$.name", is(i2.getName())))
			.andExpect(jsonPath("$.extraPrice", is((double)i2.getExtraPrice())));
	}

	@Test
	public void testModifyIngredient() throws Exception {
		
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
		
		mockMvc.perform(put("/appclient/ingredients/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Ingredient()))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotFound());
		mockMvc.perform(put("/appclient/ingredients/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Ingredient()))
					.with(httpBasicHeader))
			.andExpect(status().isForbidden());
		mockMvc.perform(put("/appclient/ingredients/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Ingredient()))
					.with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		mockMvc.perform(put("/appclient/ingredients/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Ingredient()))
					.with(wrongHeader))
			.andExpect(status().isUnauthorized());
		// End Authentication-Authorization
		
		//Initialize database
		Ingredient i1 = new Ingredient("Tomato");
		Ingredient i2 = new Ingredient("Cheese", 0.25f);
		i1 = ingredientRepository.save(i1);
		i2 = ingredientRepository.save(i2);
		
		// Modify i1
		Ingredient modifiedIngredient1 = new Ingredient(i1);
		modifiedIngredient1.setExtraPrice(0.20f);
		
		// Ingredient with given id is modified
		mockMvc.perform(put("/appclient/ingredients/" + i1.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedIngredient1))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._id", is(i1.get_id())))
			.andExpect(jsonPath("$.name", is(modifiedIngredient1.getName())))
			.andExpect(jsonPath("$.extraPrice", closeTo((double)modifiedIngredient1.getExtraPrice(), 
														UnitTestUtil.DELTA_ERROR)));
		
		// Modify p2
		Ingredient modifiedIngredient2 = new Ingredient(i2);
		modifiedIngredient2.setName("American cheese");
		
		// Ingredient with given id is modified
		mockMvc.perform(put("/appclient/ingredients/" + i2.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedIngredient2))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._id", is(i2.get_id())))
			.andExpect(jsonPath("$.name", is(modifiedIngredient2.getName())))
			.andExpect(jsonPath("$.extraPrice", is((double)modifiedIngredient2.getExtraPrice())));
		
		// Obtain ingredient list and check ingredients are correctly modified
		mockMvc.perform(get("/appclient/ingredients").with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2)));
		
		// Create new ingredients
		Ingredient i3 = new Ingredient("Bacon", 0.30f);
		Ingredient i4 = new Ingredient("Lettuce");
		i3 = ingredientRepository.save(i3);
		i4 = ingredientRepository.save(i4);
		
		Ingredient modifiedIngredient3 = new Ingredient(i3);
		modifiedIngredient3.setName("lettuce");
		
		// The new name is already being used, should be rejected
		mockMvc.perform(put("/appclient/ingredients/" + i3.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedIngredient3))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotAcceptable());
		
		Ingredient modifiedIngredient4 = new Ingredient(i4);
		modifiedIngredient4.setExtraPrice(-0.4f);
		
		// The price is invalid
		mockMvc.perform(put("/appclient/ingredients/" + i4.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedIngredient4))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotAcceptable());
		
		// Obtain ingredient list and check ingredients are correctly modified
		mockMvc.perform(get("/appclient/ingredients").with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(4)));
				
	}

	@Test
	public void testDeleteIngredient() throws Exception {
		
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
		
		mockMvc.perform(delete("/appclient/ingredients/thisIdDoesNotExist").with(wrongHeader))
			.andExpect(status().isUnauthorized());
		
		mockMvc.perform(delete("/appclient/ingredients/thisIdDoesNotExist").with(httpBasicHeader))
			.andExpect(status().isForbidden());
		
		mockMvc.perform(delete("/appclient/ingredients/thisIdDoesNotExist").with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		
		mockMvc.perform(delete("/appclient/ingredients/thisIdDoesNotExist").with(httpBasicHeaderAdmin))
			.andExpect(status().isNotFound());
		// End Authentication-Authorization
		
		//Initialize database
		Ingredient i1 = new Ingredient("Tomato");
		Ingredient i2 = new Ingredient("Cheese", 0.25f);
		i1 = ingredientRepository.save(i1);
		i2 = ingredientRepository.save(i2);
		
		// Ingredient with given id is deleted
		mockMvc.perform(delete("/appclient/ingredients/" + i1.get_id()).with(httpBasicHeaderAdmin))
				.andExpect(status().isOk());
		
		// Obtain ingredient list and check ingredients are correctly deleted
		mockMvc.perform(get("/appclient/ingredients").with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0]._id", is(i2.get_id())))
			.andExpect(jsonPath("$[0].name", is(i2.getName())))
			.andExpect(jsonPath("$[0].extraPrice", is((double)i2.getExtraPrice())));	
	}

	@Test
	public void testAddIngredient() throws Exception {
		
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
		
		mockMvc.perform(post("/appclient/ingredients")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Ingredient()))
					.with(httpBasicHeader))
			.andExpect(status().isForbidden());
		mockMvc.perform(post("/appclient/ingredients")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Ingredient()))
					.with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		mockMvc.perform(post("/appclient/ingredients")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Ingredient()))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotAcceptable());
		mockMvc.perform(post("/appclient/ingredients")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(new Ingredient()))
					.with(wrongHeader))
			.andExpect(status().isUnauthorized());
		// End Authentication-Authorization
		
		//Initialize database
		Ingredient i1 = new Ingredient("Tomato");
		Ingredient i2 = new Ingredient("Cheese", 0.25f);
		i1 = ingredientRepository.save(i1);
		i2 = ingredientRepository.save(i2);
		
		// An ingredient with this name already exists
		Ingredient i3 = new Ingredient("cheese");
		mockMvc.perform(post("/appclient/ingredients")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(i3))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotAcceptable());
		
		// This ingredient's name contains invalid characters
		Ingredient i4 = new Ingredient("Lettuce?/");
		mockMvc.perform(post("/appclient/ingredients")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(i4))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isNotAcceptable());
		
		// This ingredient is correctly created
		Ingredient i5 = new Ingredient("Bacon", 0.40f);
		mockMvc.perform(post("/appclient/ingredients")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(i5))
					.with(httpBasicHeaderAdmin))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name", is(i5.getName())))
			.andExpect(jsonPath("$.extraPrice", closeTo((double)i5.getExtraPrice(),UnitTestUtil.DELTA_ERROR)));
		
		// Obtain ingredient list and check ingredients were correctly created
		mockMvc.perform(get("/appclient/ingredients").with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(3)));
	}
	
}
