/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.burgerjavis.entities.Category;
import com.burgerjavis.entities.Ingredient;
import com.burgerjavis.entities.Order;
import com.burgerjavis.entities.OrderItem;
import com.burgerjavis.entities.Product;
import com.burgerjavis.repositories.CategoryRepository;
import com.burgerjavis.repositories.IngredientRepository;
import com.burgerjavis.repositories.OrderRepository;
import com.burgerjavis.repositories.ProductRepository;

@Component
public class DatabaseLoader {
	
	// REQUIRED REPOSITORIES
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
	
	@PostConstruct
	private void initDatabase() {
		mongoTemplate.getDb().dropDatabase();
		
		Ingredient i0 = ingredientRepository.save(new Ingredient("Pan"));
		Ingredient i1 = ingredientRepository.save(new Ingredient("Carne"));
		Ingredient i2 = ingredientRepository.save(new Ingredient("Lechuga"));
		Ingredient i3 = ingredientRepository.save(new Ingredient("Queso", 0.20f));
		Ingredient i4 = ingredientRepository.save(new Ingredient("Tomate"));
		Ingredient i5 = ingredientRepository.save(new Ingredient("Jamon York"));
		Ingredient i6 = ingredientRepository.save(new Ingredient("Jamon Serrano", 0.50f));
		Ingredient i7 = ingredientRepository.save(new Ingredient("Aceitunas"));
		Ingredient i8 = ingredientRepository.save(new Ingredient("Cebolla"));
		Ingredient i9 = ingredientRepository.save(new Ingredient("Pepinillo"));
		Ingredient i10 = ingredientRepository.save(new Ingredient("Atun"));
				
		Category c0 = categoryRepository.save(new Category("Bebida", "beverage"));
		Category c1 = categoryRepository.save(new Category("Hamburguesas", "burger"));
		Category c2 = categoryRepository.save(new Category("Cafe/Te/Infusiones", "coffee"));
		Category c3 = categoryRepository.save(new Category("Ensaladas", "salad"));
				
		Product p0 = productRepository.save(new Product("Hamburguesa", 4.50f, c1, Arrays.asList(i0, i1, i2, i3, i4, i5)));
		Product p1 = productRepository.save(new Product("Sandwich", 3.50f, Arrays.asList(i0, i3, i5)));
		Product p2 = productRepository.save(new Product("CocaCola", 2.20f, c0));
		Product p3 = productRepository.save(new Product("Cerveza", 1.25f));
		Product p4 = productRepository.save(new Product("Agua", 1.50f, c0));
		Product p5 = productRepository.save(new Product("Ensalada", 3.50f, c3, Arrays.asList(i2, i4, i7, i9, i10)));
		Product p6 = productRepository.save(new Product("Filete de Pollo", 4.20f));
		Product p7 = productRepository.save(new Product("Nestea", 2.20f, c0));
		Product p8 = productRepository.save(new Product("Cafe", 1.25f, c2));
				
		OrderItem oi0_0 = new OrderItem(p0, 2);
		OrderItem oi0_1 = new OrderItem(p1, 3);
		OrderItem oi0_2 = new OrderItem(p2, 2);
		OrderItem oi0_3 = new OrderItem(p3, 1);
		OrderItem oi0_4 = new OrderItem(p4, 3);
		
		List<OrderItem> oi0 = Arrays.asList(oi0_0, oi0_1, oi0_2, oi0_3, oi0_4);
		
		orderRepository.save(new Order("Mesa 1", oi0));
		
		OrderItem oi1_0 = new OrderItem(p5, 1);
		OrderItem oi1_1 = new OrderItem(p6, 1);
		OrderItem oi1_2 = new OrderItem(p7, 1);
		OrderItem oi1_3 = new OrderItem(p8, 1);
		
		List<OrderItem> oi1 = Arrays.asList(oi1_0, oi1_1, oi1_2, oi1_3);
		
		orderRepository.save(new Order("Mesa 2", oi1));
		
		OrderItem oi2_0 = new OrderItem(p0, 2);
		OrderItem oi2_1 = new OrderItem(p1, 1);
		OrderItem oi2_2 = new OrderItem(p2, 3);
		
		List<OrderItem> oi2 = Arrays.asList(oi2_0, oi2_1, oi2_2);
		
		orderRepository.save(new Order("Mesa 3", oi2));
		
	}

}
