/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.burgerjavis.entities.Category;
import com.burgerjavis.entities.Ingredient;
import com.burgerjavis.entities.Order;
import com.burgerjavis.entities.OrderItem;
import com.burgerjavis.entities.Product;
import com.burgerjavis.repositories.CategoryRepository;
import com.burgerjavis.repositories.IngredientRepository;
import com.burgerjavis.repositories.OrderRepository;
import com.burgerjavis.repositories.ProductRepository;
import com.burgerjavis.util.UnitTestUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={MongoTestConfiguration.class})
@WebAppConfiguration
public class BurgerJavisControllerTest {
	
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
	
	private MockMvc mockMvc;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		mongoTemplate.getDb().dropDatabase();
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	// ORDER HANDLER

	@Test
	public void testGetOrders() throws Exception {
		
		mockMvc.perform(get("/orders"))
    		.andExpect(status().isOk())
    		.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
    		.andExpect(jsonPath("$", hasSize(0)));
		
		//Initialize database
		Order order1 = new Order("Order 1");
		Order order2 = new Order("Order 2", new ArrayList<OrderItem>(), true);
		orderRepository.save(order1);
		orderRepository.save(order2);
		
		
		mockMvc.perform(get("/orders"))
        	.andExpect(status().isOk())
        	.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
        	.andExpect(jsonPath("$", hasSize(2)))
        	.andExpect(jsonPath("$[0]._id", is(order1.get_id())))
        	.andExpect(jsonPath("$[0].name", is(order1.getName())))
        	.andExpect(jsonPath("$[0].items", hasSize(order1.getItems().size())))
        	.andExpect(jsonPath("$[0].finished", is(order1.isFinished())))
        	.andExpect(jsonPath("$[1]._id", is(order2.get_id())))
        	.andExpect(jsonPath("$[1].name", is(order2.getName())))
        	.andExpect(jsonPath("$[1].items", hasSize(order2.getItems().size())))
        	.andExpect(jsonPath("$[1].finished", is(order2.isFinished())));

	}

	@Test
	public void testGetOrder() throws Exception {
		
		//Initialize database
		Order order1 = new Order("Order 1");
		Order order2 = new Order("Order 2", new ArrayList<OrderItem>(), true);
		order1 = orderRepository.save(order1);
		order2 = orderRepository.save(order2);
		
		mockMvc.perform(get("/orders/thisIdDoesNotExist"))
			.andExpect(status().isNotFound());
		
		mockMvc.perform(get("/orders/" + order2.get_id()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$._id", is(order2.get_id())))
			.andExpect(jsonPath("$.name", is(order2.getName())))
			.andExpect(jsonPath("$.items", hasSize(order2.getItems().size())))
			.andExpect(jsonPath("$.finished", is(order2.isFinished())));
		
	}

	@Test
	public void testModifyOrder() throws Exception {
		
		//Initialize database
		Order order1 = new Order("Order 1");
		Order order2 = new Order("Order 2", new ArrayList<OrderItem>(), true);
		order1 = orderRepository.save(order1);
		order2 = orderRepository.save(order2);
		
		// Modify order1
		Order modifiedOrder1 = new Order(order1);
		modifiedOrder1.setName("New order");
		modifiedOrder1.setFinished(true);
		
		mockMvc.perform(put("/orders/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedOrder1)))
			.andExpect(status().isNotFound());
				
		mockMvc.perform(put("/orders/" + order1.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedOrder1)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._id", is(order1.get_id())))
			.andExpect(jsonPath("$.name", is(modifiedOrder1.getName())))
			.andExpect(jsonPath("$.items", hasSize(modifiedOrder1.getItems().size())))
			.andExpect(jsonPath("$.finished", is(modifiedOrder1.isFinished())));;
		
		mockMvc.perform(get("/orders"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2)));
		
		// Create new orders
		Order order3 = new Order("Order 3", new ArrayList<OrderItem>(), true);
		Order order4 = new Order("Order 4");
		order3 = orderRepository.save(order3);
		order4 = orderRepository.save(order4);
		
		Order modifiedOrder2 = new Order(order3);
		modifiedOrder2.setName("order 4");
		
		// The new name is already being used, should be rejected
		mockMvc.perform(put("/orders/" + order3.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedOrder2)))
			.andExpect(status().isNotAcceptable());
		
		Order modifiedOrder3 = new Order(order4);
		modifiedOrder3.setName("Invalid-Name?");
		
		// The new name contains invalid characters
		mockMvc.perform(put("/orders/" + order4.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedOrder3)))
			.andExpect(status().isNotAcceptable());
		
		mockMvc.perform(get("/orders"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(4)));
	
	}

	@Test
	public void testDeleteOrder() throws Exception {
		
		//Initialize database
		Order order1 = new Order("Order 1");
		Order order2 = new Order("Order 2", new ArrayList<OrderItem>(), true);
		order1 = orderRepository.save(order1);
		order2 = orderRepository.save(order2);
		
		mockMvc.perform(delete("/orders/thisIdDoesNotExist"))
			.andExpect(status().isNotFound());
		
		mockMvc.perform(delete("/orders/" + order1.get_id()))
			.andExpect(status().isOk());
		
		mockMvc.perform(get("/orders"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1)));
				
	}

	@Test
	public void testAddOrder() throws Exception {
		
		//Initialize database
		Order order1 = new Order("Order 1");
		Order order2 = new Order("Order 2", new ArrayList<OrderItem>(), true);
		order1 = orderRepository.save(order1);
		order2 = orderRepository.save(order2);
		
		// An order with this name already exists, and is not finished
		Order order3 = new Order("order 1");
		
		mockMvc.perform(post("/orders/")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(order3)))
			.andExpect(status().isNotAcceptable());
		
		// An order with this name already exists, but it is finished
		Order order4 = new Order("Order 2");
		
		mockMvc.perform(post("/orders/")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(order4)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.name", is(order4.getName())))
			.andExpect(jsonPath("$.items", hasSize(order4.getItems().size())))
			.andExpect(jsonPath("$.finished", is(order4.isFinished())));
		
		// The order name contains invalid characters
		Order order5 = new Order("Order 5??");
		
		mockMvc.perform(post("/orders/")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(order5)))
			.andExpect(status().isNotAcceptable());
		
		// An order with this name already exists, but it is finished
		Order order6 = new Order("Order 6", new ArrayList<OrderItem>(), true);
		
		mockMvc.perform(post("/orders/")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(order6)))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.name", is(order6.getName())))
			.andExpect(jsonPath("$.items", hasSize(order6.getItems().size())))
			.andExpect(jsonPath("$.finished", is(order6.isFinished())));

	}
	
	// PRODUCT HANDLER

	@Test
	public void testGetProducts() throws Exception {
		
		mockMvc.perform(get("/products"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(0)));
		
		//Initialize database
		Product p1 = new Product("Burger", 4.5f);
		Category category = new Category("Sandwiches", "sandwich");
		Product p2 = new Product("Sandwich", 5.0f, category);
		productRepository.save(p1);
		productRepository.save(p2);
		
		mockMvc.perform(get("/products"))
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
		// Initialize database
		Product p1 = new Product("Burger", 4.5f);
		Category category = new Category("Sandwiches", "sandwich");
		Product p2 = new Product("Sandwich", 5.0f, category);
		productRepository.save(p1);
		productRepository.save(p2);
		
		mockMvc.perform(get("/products/thisIdDoesNotExist"))
			.andExpect(status().isNotFound());
		
		mockMvc.perform(get("/products/" + p2.get_id()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$._id", is(p2.get_id())))
			.andExpect(jsonPath("$.name", is(p2.getName())))
			.andExpect(jsonPath("$.ingredients", hasSize(p2.getIngredients().size())));
		
	}

	@Test
	public void testModifyProduct() throws Exception {
		
		// Initialize database
		Product p1 = new Product("Burger", 4.5f);
		Category category = new Category("Sandwiches", "sandwich");
		Product p2 = new Product("Sandwich", 5.0f, category);
		productRepository.save(p1);
		productRepository.save(p2);
		
		// Modify p2
		Product modifiedProduct1 = new Product(p2);
		modifiedProduct1.setName("MegaSandwich");
		
		mockMvc.perform(put("/products/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedProduct1)))
			.andExpect(status().isNotFound());
		
		mockMvc.perform(put("/products/" + p2.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedProduct1)))
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
		
		mockMvc.perform(put("/products/" + p1.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedProduct2)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._id", is(p1.get_id())))
			.andExpect(jsonPath("$.name", is(modifiedProduct2.getName())))
			.andExpect(jsonPath("$.price", is((double)modifiedProduct2.getPrice())))
			.andExpect(jsonPath("$.category.name", is(modifiedProduct2.getCategory().getName())))
			.andExpect(jsonPath("$.category.icon", is(modifiedProduct2.getCategory().getIcon())))
			.andExpect(jsonPath("$.ingredients", hasSize(modifiedProduct2.getIngredients().size())));
		
		mockMvc.perform(get("/products"))
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
		mockMvc.perform(put("/products/" + p3.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedProduct3)))
			.andExpect(status().isNotAcceptable());
		
		Product modifiedProduct4 = new Product(p4);
		modifiedProduct4.setPrice(-4.1f);
		
		// The new price is invalid
		mockMvc.perform(put("/products/" + p4.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedProduct4)))
			.andExpect(status().isNotAcceptable());
		
		mockMvc.perform(get("/products"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(4)));
				
	}

	@Test
	public void testDeleteProduct() throws Exception {
		// Initialize database
		Product p1 = new Product("Burger", 4.5f);
		Category category = new Category("Sandwiches", "sandwich");
		Product p2 = new Product("Sandwich", 5.0f, category);
		productRepository.save(p1);
		productRepository.save(p2);
		
		mockMvc.perform(delete("/products/thisIdDoesNotExist"))
			.andExpect(status().isNotFound());
		
		mockMvc.perform(delete("/products/" + p1.get_id()))
				.andExpect(status().isOk());
		
		mockMvc.perform(get("/products"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1)));		
	}

	@Test
	public void testAddProduct() throws Exception {
		// Initialize database
		Product p1 = new Product("Burger", 4.5f);
		Category category = new Category("Sandwiches", "sandwich");
		Product p2 = new Product("Sandwich", 5.0f, category);
		productRepository.save(p1);
		productRepository.save(p2);
		
		// A product with this name already exists
		Product p3 = new Product("Burger", 5.6f);
		mockMvc.perform(post("/products")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(p3)))
			.andExpect(status().isNotAcceptable());
		
		// The product name contains invalid characters
		Product p4 = new Product("Burger??", 5.6f);
		mockMvc.perform(post("/products")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(p4)))
			.andExpect(status().isNotAcceptable());
		
		Product p5 = new Product("Hotdog", 3.50f);
		mockMvc.perform(post("/products")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(p5)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name", is(p5.getName())))
			.andExpect(jsonPath("$.price", is((double)p5.getPrice())))
			.andExpect(jsonPath("$.category.name", is(p5.getCategory().getName())))
			.andExpect(jsonPath("$.category.icon", is(p5.getCategory().getIcon())))
			.andExpect(jsonPath("$.ingredients", hasSize(p5.getIngredients().size())));
		
		mockMvc.perform(get("/products"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(3)));
	}
	
	// INGREDIENT HANDLER

	@Test
	public void testGetIngredients() throws Exception {
		
		mockMvc.perform(get("/ingredients"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(0)));
		
		//Initialize database
		Ingredient i1 = new Ingredient("Tomato");
		Ingredient i2 = new Ingredient("Cheese", 0.25f);
		ingredientRepository.save(i1);
		ingredientRepository.save(i2);
		
		mockMvc.perform(get("/ingredients"))
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
		//Initialize database
		Ingredient i1 = new Ingredient("Tomato");
		Ingredient i2 = new Ingredient("Cheese", 0.25f);
		i1 = ingredientRepository.save(i1);
		i2 = ingredientRepository.save(i2);
		
		mockMvc.perform(get("/ingredients/thisIdDoesNotExist"))
			.andExpect(status().isNotFound());
		
		mockMvc.perform(get("/ingredients/" + i2.get_id()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$._id", is(i2.get_id())))
			.andExpect(jsonPath("$.name", is(i2.getName())))
			.andExpect(jsonPath("$.extraPrice", is((double)i2.getExtraPrice())));
	}

	@Test
	public void testModifyIngredient() throws Exception {
		//Initialize database
		Ingredient i1 = new Ingredient("Tomato");
		Ingredient i2 = new Ingredient("Cheese", 0.25f);
		i1 = ingredientRepository.save(i1);
		i2 = ingredientRepository.save(i2);
		
		// Modify i1
		Ingredient modifiedIngredient1 = new Ingredient(i1);
		modifiedIngredient1.setExtraPrice(0.20f);
		
		mockMvc.perform(put("/ingredients/thisIdDoesNotExist")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedIngredient1)))
			.andExpect(status().isNotFound());
		
		mockMvc.perform(put("/ingredients/" + i1.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedIngredient1)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._id", is(i1.get_id())))
			.andExpect(jsonPath("$.name", is(modifiedIngredient1.getName())))
			.andExpect(jsonPath("$.extraPrice", closeTo((double)modifiedIngredient1.getExtraPrice(), 
														UnitTestUtil.DELTA_ERROR)));
		
		// Modify p2
		Ingredient modifiedIngredient2 = new Ingredient(i2);
		modifiedIngredient2.setName("American cheese");
		
		mockMvc.perform(put("/ingredients/" + i2.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedIngredient2)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$._id", is(i2.get_id())))
			.andExpect(jsonPath("$.name", is(modifiedIngredient2.getName())))
			.andExpect(jsonPath("$.extraPrice", is((double)modifiedIngredient2.getExtraPrice())));
		
		mockMvc.perform(get("/ingredients"))
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
		mockMvc.perform(put("/ingredients/" + i3.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedIngredient3)))
			.andExpect(status().isNotAcceptable());
		
		Ingredient modifiedIngredient4 = new Ingredient(i4);
		modifiedIngredient4.setExtraPrice(-0.4f);
		
		// The price is invalid
		mockMvc.perform(put("/ingredients/" + i4.get_id())
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(modifiedIngredient4)))
			.andExpect(status().isNotAcceptable());
		
		mockMvc.perform(get("/ingredients"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(4)));
				
	}

	@Test
	public void testDeleteIngredient() throws Exception {
		//Initialize database
		Ingredient i1 = new Ingredient("Tomato");
		Ingredient i2 = new Ingredient("Cheese", 0.25f);
		i1 = ingredientRepository.save(i1);
		i2 = ingredientRepository.save(i2);
		
		mockMvc.perform(delete("/ingredients/thisIdDoesNotExist"))
			.andExpect(status().isNotFound());
		
		mockMvc.perform(delete("/ingredients/" + i1.get_id()))
				.andExpect(status().isOk());
		
		mockMvc.perform(get("/ingredients"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0]._id", is(i2.get_id())))
			.andExpect(jsonPath("$[0].name", is(i2.getName())))
			.andExpect(jsonPath("$[0].extraPrice", is((double)i2.getExtraPrice())));	
	}

	@Test
	public void testAddIngredient() throws Exception {
		//Initialize database
		Ingredient i1 = new Ingredient("Tomato");
		Ingredient i2 = new Ingredient("Cheese", 0.25f);
		i1 = ingredientRepository.save(i1);
		i2 = ingredientRepository.save(i2);
		
		// An ingredient with this name already exists
		Ingredient i3 = new Ingredient("cheese");
		mockMvc.perform(post("/ingredients")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(i3)))
			.andExpect(status().isNotAcceptable());
		
		// This ingredient's name contains invalid characters
		Ingredient i4 = new Ingredient("Lettuce?/");
		mockMvc.perform(post("/products")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(i4)))
			.andExpect(status().isNotAcceptable());
		
		Ingredient i5 = new Ingredient("Bacon", 0.40f);
		mockMvc.perform(post("/ingredients")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(i5)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name", is(i5.getName())))
			.andExpect(jsonPath("$.extraPrice", closeTo((double)i5.getExtraPrice(),UnitTestUtil.DELTA_ERROR)));
		
		mockMvc.perform(get("/ingredients"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(3)));
	}
	

	// CATEGORY HANDLER
	
	@Test
	public void testGetCategories() throws Exception {
		mockMvc.perform(get("/categories"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(0)));
		
		//Initialize database
		Category c1 = new Category("Burgers", "burger", false);
		Category c2 = new Category("Sandwiches", "sandwich", true);
		c1 = categoryRepository.save(c1);
		c2 = categoryRepository.save(c2);
		
		mockMvc.perform(get("/categories"))
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
		//Initialize database
		Category c1 = new Category("Burgers", "burger", false);
		Category c2 = new Category("Sandwiches", "sandwich", true);
		c1 = categoryRepository.save(c1);
		c2 = categoryRepository.save(c2);
		
		mockMvc.perform(get("/categories/thisIdDoesNotExist"))
			.andExpect(status().isNotFound());
		
		mockMvc.perform(get("/categories/" + c2.get_id()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$._id", is(c2.get_id())))
			.andExpect(jsonPath("$.name", is(c2.getName())))
			.andExpect(jsonPath("$.icon", is(c2.getIcon())))
			.andExpect(jsonPath("$.favorite", is(c2.isFavorite())));
		
	}

	@Ignore
	@Test
	public void testModifyCategory() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteCategory() throws Exception {
		//Initialize database
		Category c1 = new Category("Burgers", "burger", false);
		Category c2 = new Category("Sandwiches", "sandwich", true);
		c1 = categoryRepository.save(c1);
		c2 = categoryRepository.save(c2);
		
		mockMvc.perform(delete("/categories/thisIdDoesNotExist"))
			.andExpect(status().isNotFound());
		
		mockMvc.perform(delete("/categories/" + c1.get_id()))
				.andExpect(status().isOk());
		
		mockMvc.perform(get("/categories"))
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
		//Initialize database
		Category c1 = new Category("Burgers", "burger", true);
		Category c2 = new Category("Sandwiches", "sandwich", true);
		c1 = categoryRepository.save(c1);
		c2 = categoryRepository.save(c2);
		
		// A category with this name already exists
		Category c3 = new Category("burgers", "drink", false);
		mockMvc.perform(post("/categories")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(c3)))
			.andExpect(status().isNotAcceptable());
		
		Category c4 = new Category("Pizzas", "pizza", true);
		mockMvc.perform(post("/categories")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(c4)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name", is(c4.getName())))
			.andExpect(jsonPath("$.icon", is(c4.getIcon())))
			.andExpect(jsonPath("$.favorite", is(c4.isFavorite())));
		
		mockMvc.perform(get("/categories"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(UnitTestUtil.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(3)));
		
		// There are 3 favorite categories already
		Category c5 = new Category("Beers", "beer", true);
		mockMvc.perform(post("/categories")
				.contentType(UnitTestUtil.APPLICATION_JSON_UTF8)
				.content(UnitTestUtil.convertObjectToJson(c5)))
			.andExpect(status().isNotAcceptable());
	}
	
	@After
	public void tearDown() throws Exception {
		mongoTemplate.getDb().dropDatabase();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

}
