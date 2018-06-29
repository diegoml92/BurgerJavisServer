/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.burgerjavis.entities.Category;
import com.burgerjavis.entities.Ingredient;
import com.burgerjavis.entities.Order;
import com.burgerjavis.entities.OrderItem;
import com.burgerjavis.entities.Product;
import com.burgerjavis.entities.User;
import com.burgerjavis.validation.CategoryValidator;
import com.burgerjavis.validation.IngredientValidator;
import com.burgerjavis.validation.OrderItemValidator;
import com.burgerjavis.validation.OrderValidator;
import com.burgerjavis.validation.ProductValidator;
import com.burgerjavis.validation.UserValidator;
import com.burgerjavis.validation.ValidationPatterns;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={MongoTestConfiguration.class})
@WebAppConfiguration
public class BurgerJavisServerApplicationTest {
	
	@Test
	public void testIngredientValidator() throws Exception {
		
		// Just for coverage purposes
		@SuppressWarnings("unused")
		IngredientValidator ingredientValidator = new IngredientValidator();
		
		@SuppressWarnings("unused")
		ValidationPatterns validationPatterns = new ValidationPatterns();
		
		Ingredient ingredient1 = new Ingredient();				// Empty ingredient
		Ingredient ingredient2 = new Ingredient("");			// Empty name
		Ingredient ingredient3 = new Ingredient("Ingredient?");	// Invalid name
		Ingredient ingredient4 = new Ingredient("Ingredient");	// Named ingredient
		
		// Invalid ingredients
		assertFalse(IngredientValidator.validateIngredient(ingredient1));
		assertFalse(IngredientValidator.validateIngredient(ingredient2));
		assertFalse(IngredientValidator.validateIngredient(ingredient3));
		
		// Valid ingredient
		assertTrue(IngredientValidator.validateIngredient(ingredient4));
		
	}
	
	@Test
	public void testCategoryValidator() throws Exception {
		
		// Just for coverage purposes
		@SuppressWarnings("unused")
		CategoryValidator categoryValidator = new CategoryValidator();
		
		Category category1 = new Category();			// Empty category
		Category category2 = new Category("");			// Empty category name
		Category category3 = new Category("Category");	// Named category
		
		assertTrue(CategoryValidator.validateCategory(category1));
		assertTrue(CategoryValidator.validateCategory(category2));
		assertTrue(CategoryValidator.validateCategory(category3));
		
	}
	
	@Test
	public void testUserValidator() throws Exception {
		
		// Just for coverage purposes
		@SuppressWarnings("unused")
		UserValidator userValidator = new UserValidator();

		List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
		roles.add(new SimpleGrantedAuthority("ROLE"));
		
		User user1 = new User(); 								// Empty user
		User user2 = new User("Username?", "password"); 		// Invalid username
		User user3 = new User("Username", "pas");				// Invalid password
		User user4 = new User("Username", "password"); 			// Empty role
		User user5 = new User("Username", "password", roles);	// Valid user
		
		// Invalid users
		assertFalse(UserValidator.validateUser(user1));
		assertFalse(UserValidator.validateUser(user2));
		assertFalse(UserValidator.validateUser(user3));
		assertFalse(UserValidator.validateUser(user4));
		
		// Valid user
		assertTrue(UserValidator.validateUser(user5));
		
	}
	
	@Test
	public void testProductValidator() throws Exception {
		
		// Just for coverage purposes
		@SuppressWarnings("unused")
		ProductValidator productValidator = new ProductValidator();
		
		Ingredient ingredient1 = new Ingredient("Ingredient?");
		Ingredient ingredient2 = new Ingredient("Ingredient");
		
		List<Ingredient> ingredients1 = new ArrayList<Ingredient>();
		List<Ingredient> ingredients2 = new ArrayList<Ingredient>();
		
		ingredients1.add(ingredient1);
		ingredients2.add(ingredient2);
		
		Category category1 = new Category("Category");
		
		Product product1 = new Product();								// Empty product
		Product product2 = new Product("Product?", 5.0f);				// Invalid name
		Product product3 = new Product("", 5.0f);						// Empty name
		Product product4 = new Product("Product", -1.0f);				// Invalid price
		Product product5 = new Product("Product", 5.0f, ingredients1);	// Invalid ingredient
		
		Product product6 = new Product("Product", 5.0f, ingredients2);				// Valid product without category
		Product product7 = new Product("Product", 5.0f, category1);					// Valid product without ingredients
		Product product8 = new Product("Product", 5.0f);							// Valid product no category or ingredients
		Product product9 = new Product("Product", 5.0f, category1, ingredients2);	// Valid product
		
		// Invalid products
		assertFalse(ProductValidator.validateProduct(product1));
		assertFalse(ProductValidator.validateProduct(product2));
		assertFalse(ProductValidator.validateProduct(product3));
		assertFalse(ProductValidator.validateProduct(product4));
		assertFalse(ProductValidator.validateProduct(product5));
		
		// Valid products
		assertTrue(ProductValidator.validateProduct(product6));
		assertTrue(ProductValidator.validateProduct(product7));
		assertTrue(ProductValidator.validateProduct(product8));
		assertTrue(ProductValidator.validateProduct(product9));
		
	}
	
	@Test
	public void testOrderItemValidator() throws Exception {
		
		// Just for coverage purposes
		@SuppressWarnings("unused")
		OrderItemValidator orderItemValidator = new OrderItemValidator();
		
		Product product1 = new Product("Product?", 5.0f);
		Product product2 = new Product("Product", 5.0f);
		
		OrderItem orderItem1 = new OrderItem();				// Empty OrderItem
		OrderItem orderItem2 = new OrderItem(product1, 1);	// Invalid product
		OrderItem orderItem3 = new OrderItem(product2, -1);	// Invalid amount
		OrderItem orderItem4 = new OrderItem(product2, 1);	// Valid OrderItem
		
		// Invalid order items
		assertFalse(OrderItemValidator.validateOrderItem(orderItem1));
		assertFalse(OrderItemValidator.validateOrderItem(orderItem2));
		assertFalse(OrderItemValidator.validateOrderItem(orderItem3));
		
		// Valid order item
		assertTrue(OrderItemValidator.validateOrderItem(orderItem4));
		
	}
	
	@Test
	public void testOrderValidator() throws Exception {
		
		// Just for coverage purposes
		@SuppressWarnings("unused")
		OrderValidator orderValidator = new OrderValidator();
		
		Product product = new Product("Product", 5.0f);
		
		OrderItem orderItem1 = new OrderItem();
		OrderItem orderItem2 = new OrderItem(product, 1);
		
		List<OrderItem> orderItems1 = new ArrayList<OrderItem>();
		List<OrderItem> orderItems2 = new ArrayList<OrderItem>();
		
		orderItems1.add(orderItem1);
		orderItems2.add(orderItem2);
		
		Order order1 = new Order();									// Empty order
		Order order2 = new Order("Order?", "username");				// Invalid name
		Order order3 = new Order("Order", "");						// Empty username
		Order order4 = new Order("Order", "user?name");				// Invalid username
		Order order5 = new Order("Order", orderItems1, "username");	// Invalid order items
		Order order6 = new Order("Order", "username");				// Valid order without order items
		Order order7 = new Order("Order", orderItems2, "username");	// Valid order
		
		// Invalid orders
		assertFalse(OrderValidator.validateOrder(order1));
		assertFalse(OrderValidator.validateOrder(order2));
		assertFalse(OrderValidator.validateOrder(order3));
		assertFalse(OrderValidator.validateOrder(order4));
		assertFalse(OrderValidator.validateOrder(order5));
		
		// Valid orders
		assertTrue(OrderValidator.validateOrder(order6));
		assertTrue(OrderValidator.validateOrder(order7));
		
	}

}
