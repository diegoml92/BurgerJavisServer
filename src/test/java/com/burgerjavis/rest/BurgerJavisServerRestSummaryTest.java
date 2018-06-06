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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

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
import com.burgerjavis.entities.Category;
import com.burgerjavis.entities.Ingredient;
import com.burgerjavis.entities.Order;
import com.burgerjavis.entities.OrderItem;
import com.burgerjavis.entities.Product;
import com.burgerjavis.entities.User;
import com.burgerjavis.repositories.CategoryRepository;
import com.burgerjavis.repositories.IngredientRepository;
import com.burgerjavis.repositories.OrderRepository;
import com.burgerjavis.repositories.ProductRepository;
import com.burgerjavis.repositories.UserRepository;
import com.burgerjavis.util.UnitTestUtil;
import com.burgerjavis.util.UnitTestUtil.UserRole;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={MongoTestConfiguration.class})
@WebAppConfiguration
public class BurgerJavisServerRestSummaryTest {
	
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private IngredientRepository ingredientRepository;
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
	
	@Test
	public void testGetSummaryData() throws Exception {
		
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
		
		mockMvc.perform(get("/appclient/summary").with(wrongHeader))
			.andExpect(status().isUnauthorized());
		mockMvc.perform(get("/appclient/summary").with(httpBasicHeader))
			.andExpect(status().isForbidden());
		mockMvc.perform(get("/appclient/summary").with(httpBasicHeaderUser2))
			.andExpect(status().isForbidden());
		mockMvc.perform(get("/appclient/summary").with(httpBasicHeaderAdmin))
			.andExpect(status().isNoContent());
		// End Authentication-Authorization
		
		Ingredient i0 = ingredientRepository.save(new Ingredient("Pan"));
		Ingredient i1 = ingredientRepository.save(new Ingredient("Carne"));
		Ingredient i2 = ingredientRepository.save(new Ingredient("Lechuga"));
		Ingredient i3 = ingredientRepository.save(new Ingredient("Queso"));
		Ingredient i4 = ingredientRepository.save(new Ingredient("Tomate"));
		Ingredient i5 = ingredientRepository.save(new Ingredient("Jamon York"));
		Ingredient i6 = ingredientRepository.save(new Ingredient("Jamon Serrano"));
		Ingredient i7 = ingredientRepository.save(new Ingredient("Aceitunas"));
		Ingredient i8 = ingredientRepository.save(new Ingredient("Cebolla"));
		Ingredient i9 = ingredientRepository.save(new Ingredient("Pepinillo"));
		Ingredient i10 = ingredientRepository.save(new Ingredient("Atun"));
		Ingredient i11 = ingredientRepository.save(new Ingredient("Tomate"));
				
		Category c0 = categoryRepository.save(new Category("Bebida", true));
		Category c1 = categoryRepository.save(new Category("Hamburguesas"));
		Category c2 = categoryRepository.save(new Category("Cafe/Te/Infusiones"));
		Category c3 = categoryRepository.save(new Category("Ensaladas", true));
		Category c4 = categoryRepository.save(new Category("Tostas"));
				
		Product p0 = productRepository.save(new Product("Hamburguesa", 4.50f, c1, Arrays.asList(i0, i1, i2, i3, i4, i5)));
		Product p1 = productRepository.save(new Product("Sandwich", 3.50f, Arrays.asList(i0, i3, i5)));
		Product p2 = productRepository.save(new Product("CocaCola", 2.20f, c0));
		Product p3 = productRepository.save(new Product("Cerveza", 1.25f));
		Product p4 = productRepository.save(new Product("Agua", 1.50f, c0));
		Product p5 = productRepository.save(new Product("Ensalada", 3.50f, c3, Arrays.asList(i2, i4, i7, i9, i10)));
		Product p6 = productRepository.save(new Product("Filete de Pollo", 4.20f));
		Product p7 = productRepository.save(new Product("Nestea", 2.20f, c0));
		Product p8 = productRepository.save(new Product("Cafe", 1.25f, c2));
		Product p9 = productRepository.save(new Product("Tosta jamon", 3.50f, c4, Arrays.asList(i6, i8, i11)));
				
		OrderItem oi0_0 = new OrderItem(p0, 2);
		OrderItem oi0_1 = new OrderItem(p1, 3);
		OrderItem oi0_2 = new OrderItem(p2, 2);
		OrderItem oi0_3 = new OrderItem(p3, 1);
		OrderItem oi0_4 = new OrderItem(p4, 3);
		
		List<OrderItem> oi0 = Arrays.asList(oi0_0, oi0_1, oi0_2, oi0_3, oi0_4);
		
		orderRepository.save(new Order("Mesa 1", oi0, "user1"));
		
		OrderItem oi1_0 = new OrderItem(p5, 1);
		OrderItem oi1_1 = new OrderItem(p6, 1);
		OrderItem oi1_2 = new OrderItem(p7, 1);
		OrderItem oi1_3 = new OrderItem(p8, 1);
		
		List<OrderItem> oi1 = Arrays.asList(oi1_0, oi1_1, oi1_2, oi1_3);
		
		orderRepository.save(new Order("Mesa 2", oi1, "user1"));
		
		OrderItem oi2_0 = new OrderItem(p0, 2);
		OrderItem oi2_1 = new OrderItem(p1, 1);
		OrderItem oi2_2 = new OrderItem(p2, 3);
		
		List<OrderItem> oi2 = Arrays.asList(oi2_0, oi2_1, oi2_2);
		
		orderRepository.save(new Order("Mesa 3", oi2, "user2"));
		
		// There is not info since there are not finished orders
		mockMvc.perform(get("/appclient/summary").with(httpBasicHeaderAdmin))
			.andExpect(status().isNoContent());
		
		OrderItem oi3_0 = new OrderItem(p9, 2);
		OrderItem oi3_1 = new OrderItem(p3, 2);
		
		List<OrderItem> oi3 = Arrays.asList(oi3_0, oi3_1);
		
		orderRepository.save(new Order("Mesa 4", oi3, OrderState.FINISHED, "user1"));
		
		OrderItem oi4_0 = new OrderItem(p0, 5);
		OrderItem oi4_1 = new OrderItem(p1, 3);
		OrderItem oi4_2 = new OrderItem(p2, 2);
		OrderItem oi4_3 = new OrderItem(p3, 1);
		OrderItem oi4_4 = new OrderItem(p4, 5);
		OrderItem oi4_5 = new OrderItem(p5, 2);
		
		List<OrderItem> oi4 = Arrays.asList(oi4_0, oi4_1, oi4_2, oi4_3, oi4_4, oi4_5);
		
		orderRepository.save(new Order("Mesa 5", oi4, OrderState.FINISHED, "user2"));
		
		// Check retrieved summary data is correct
		mockMvc.perform(get("/appclient/summary").with(httpBasicHeaderAdmin))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.profits", closeTo(62.65, UnitTestUtil.DELTA_ERROR)))
			.andExpect(jsonPath("$.completedOrders", is(2)))
			.andExpect(jsonPath("$.topCategories", hasSize(3)))
			.andExpect(jsonPath("$.topProducts[0]", hasSize(2)))
			.andExpect(jsonPath("$.topProducts[1]", hasSize(1)))
			.andExpect(jsonPath("$.topProducts[2]", hasSize(4)));
		
	}
	
}
