/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.burgerjavis.entities.Category;
import com.burgerjavis.entities.Ingredient;
import com.burgerjavis.entities.Order;
import com.burgerjavis.entities.Product;
import com.burgerjavis.entities.User;
import com.burgerjavis.repositories.CategoryRepository;
import com.burgerjavis.repositories.IngredientRepository;
import com.burgerjavis.repositories.OrderRepository;
import com.burgerjavis.repositories.ProductRepository;
import com.burgerjavis.repositories.UserRepository;
import com.burgerjavis.summary.SummaryData;
import com.burgerjavis.validation.CategoryValidator;
import com.burgerjavis.validation.IngredientValidator;
import com.burgerjavis.validation.OrderValidator;
import com.burgerjavis.validation.ProductValidator;

@RestController
public class BurgerJavisController {
	
	// REQUIRED SERVICES
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
	  
	// ORDER HANDLER

	/* Return order list */
	@Secured ({"ROLE_WAITER", "ROLE_ADMIN"})
	@RequestMapping (value = "/orders", method = RequestMethod.GET)
	public ResponseEntity<List<Order>> getOrders(Principal principal) {
		List<Order> orders = null;
		try {
			orders = (List<Order>) orderRepository.
					findByUsernameIgnoreCaseAndFinishedFalse(principal.getName());
			return new ResponseEntity<List<Order>>(orders, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<Order>>(orders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Return referenced order */
	@Secured ({"ROLE_WAITER", "ROLE_ADMIN"})
	@RequestMapping (value = "/orders/{id}", method = RequestMethod.GET)
	public ResponseEntity<Order> getOrder(@PathVariable ("id") String id, Principal principal) {
		Order order = null;
		try {
			order = orderRepository.findOne(id);
			if(order == null) {
				return new ResponseEntity<Order>(order, HttpStatus.NOT_FOUND);
			}
			if(!order.getUsername().equalsIgnoreCase(principal.getName())) {
				return new ResponseEntity<Order>(order, HttpStatus.UNAUTHORIZED);
			}
			if(order.isFinished()) {
				return new ResponseEntity<Order>(order, HttpStatus.FORBIDDEN);
			}
			return new ResponseEntity<Order>(order, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Order>(order, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Modify existing order */
	@Secured ({"ROLE_WAITER", "ROLE_ADMIN"})
	@RequestMapping (value = "/orders/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Order> modifyOrder
			(@PathVariable ("id") String id, @RequestBody Order order, Principal principal) {
		Order modifiedOrder = null;
		try {
			Order currentOrder = orderRepository.findOne(id);
			if(currentOrder == null) {
				// Order not found
				return new ResponseEntity<Order>(modifiedOrder, HttpStatus.NOT_FOUND);
			}
			if(!currentOrder.getUsername().equalsIgnoreCase(principal.getName())) {
				// Unauthorized user
				return new ResponseEntity<Order>(modifiedOrder, HttpStatus.UNAUTHORIZED);
			}
			if(currentOrder.isFinished()) {
				return new ResponseEntity<Order>(modifiedOrder, HttpStatus.FORBIDDEN);
			}
			if(!OrderValidator.validateOrder(order)) {
				// Order not valid
				return new ResponseEntity<Order>(modifiedOrder, HttpStatus.NOT_ACCEPTABLE);
			}
			if(!order.getName().equalsIgnoreCase(currentOrder.getName())) {
				// Name has been modified, check if new name is available
				List<Order> conflictingOrders = orderRepository.findByNameIgnoreCase(order.getName());
				boolean conflict = false;
				int i=0;
				while (!conflict && i<conflictingOrders.size()) {
					conflict = !conflictingOrders.get(i).isFinished();
					i++;
				}
				if (conflict) {
					// New name is already being used
					return new ResponseEntity<Order>(modifiedOrder, HttpStatus.NOT_ACCEPTABLE);
				}
			}
			currentOrder.updateOrder(order);
			modifiedOrder = orderRepository.save(currentOrder);
			return new ResponseEntity<Order>(modifiedOrder, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Order>(modifiedOrder, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Delete referenced order */
	@Secured ({"ROLE_WAITER", "ROLE_ADMIN"})
	@RequestMapping (value = "/orders/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Boolean> deleteOrder (@PathVariable ("id") String id, Principal principal) {
		try {
			Order currentOrder = orderRepository.findOne(id);
			if(currentOrder == null) {
				return new ResponseEntity<Boolean>(false, HttpStatus.NOT_FOUND);
			}
			if(!currentOrder.getUsername().equalsIgnoreCase(principal.getName())) {
				// Unauthorized user
				return new ResponseEntity<Boolean>(false, HttpStatus.UNAUTHORIZED);
			}
			if(currentOrder.isFinished()) {
				return new ResponseEntity<Boolean>(false, HttpStatus.FORBIDDEN);
			}
			orderRepository.delete(currentOrder);
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Boolean>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Create new order */
	@Secured ({"ROLE_WAITER", "ROLE_ADMIN"})
	@RequestMapping (value = "/orders", method = RequestMethod.POST)
	public ResponseEntity<Order> addOrder(@RequestBody Order order, Principal principal) {
		Order newOrder = null;
		try {
			if(!OrderValidator.validateOrder(order)) {
				// Order not valid
				return new ResponseEntity<Order>(newOrder, HttpStatus.NOT_ACCEPTABLE);
			}
			if(!order.getUsername().equalsIgnoreCase(principal.getName())) {
				// Incoherent data
				return new ResponseEntity<Order>(newOrder, HttpStatus.NOT_ACCEPTABLE);
			}
			List<Order> orders = orderRepository.findByNameIgnoreCase(order.getName());
			// Order name can be reused if previous order with same name is finished
			boolean create = true;
			if(orders.size() > 0) {
				int i = 0;
				while(create && i < orders.size()) {
					create = orders.get(i).isFinished();
					i++;
				}
			}
			if (!create) {
				return new ResponseEntity<Order>(newOrder, HttpStatus.NOT_ACCEPTABLE);
			}
			newOrder = orderRepository.save(order);
			return new ResponseEntity<Order>(newOrder, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<Order>(newOrder, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	// PRODUCT HANDLER

	/* Return product list */
	@Secured ({"ROLE_WAITER", "ROLE_ADMIN"})
	@RequestMapping (value = "/products", method = RequestMethod.GET)
	public ResponseEntity<List<Product>> getProducts() {
		List<Product> products = null;
		try {
			products = (List<Product>) productRepository.findAll();
			return new ResponseEntity<List<Product>>(products, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<Product>>(products, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Return referenced product */
	@Secured ("ROLE_ADMIN")
	@RequestMapping (value = "/products/{id}", method = RequestMethod.GET)
	public ResponseEntity<Product> getProduct(@PathVariable ("id") String id) {
		Product product = null;
		try {
			product = productRepository.findOne(id);
			HttpStatus httpStatus = product != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
			return new ResponseEntity<Product>(product, httpStatus);
		} catch (Exception e) {
			return new ResponseEntity<Product>(product, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Modify existing product */
	@Secured ("ROLE_ADMIN")
	@RequestMapping (value = "/products/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Product> modifyProduct
			(@PathVariable ("id") String id, @RequestBody Product product) {
		Product modifiedProduct = null;
		try {
			Product currentProduct = productRepository.findOne(id);
			if(currentProduct == null) {
				return new ResponseEntity<Product>(modifiedProduct, HttpStatus.NOT_FOUND);
			}
			if(!ProductValidator.validateProduct(product)) {
				// Given product is not valid
				return new ResponseEntity<Product>(modifiedProduct, HttpStatus.NOT_ACCEPTABLE);
			}
			// Check if name is modified
			if(!product.getName().equalsIgnoreCase(currentProduct.getName())) {
				// Name has been modified
				if(productRepository.findByNameIgnoreCase(product.getName()).size() > 0) {
					// A product with this name already exists
					return new ResponseEntity<Product>(modifiedProduct, HttpStatus.NOT_ACCEPTABLE);
				}
			}
			currentProduct.updateProduct(product);
			modifiedProduct = productRepository.save(currentProduct);
			return new ResponseEntity<Product>(modifiedProduct, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Product>(modifiedProduct, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Delete referenced product */
	@Secured ("ROLE_ADMIN")
	@RequestMapping (value = "/products/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Boolean> deleteProduct (@PathVariable ("id") String id) {
		try {
			Product currentProduct = productRepository.findOne(id);
			if(currentProduct == null) {
				return new ResponseEntity<Boolean>(false, HttpStatus.NOT_FOUND);
			}
			productRepository.delete(currentProduct);
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Boolean>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Create new product */
	@Secured ("ROLE_ADMIN")
	@RequestMapping (value = "/products", method = RequestMethod.POST)
	public ResponseEntity<Product> addProduct(@RequestBody Product product) {
		Product newProduct = null;
		try{
			if(!ProductValidator.validateProduct(product)) {
				// Given product is not valid
				return new ResponseEntity<Product>(newProduct, HttpStatus.NOT_ACCEPTABLE);
			}
			if(productRepository.findByNameIgnoreCase(product.getName()).size() > 0) {
				// A product with this name already exists
				return new ResponseEntity<Product>(newProduct, HttpStatus.NOT_ACCEPTABLE);
			}
			newProduct = productRepository.save(product);
			return new ResponseEntity<Product>(newProduct, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<Product>(newProduct, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	// INGREDIENT HANDLER

	/* Return ingredients list */
	@Secured ("ROLE_ADMIN")
	@RequestMapping (value = "/ingredients", method = RequestMethod.GET)
	public ResponseEntity<List<Ingredient>> getIngredients() {
		List<Ingredient> ingredients = null;
		try {
			ingredients = (List<Ingredient>) ingredientRepository.findAll();
			return new ResponseEntity<List<Ingredient>>(ingredients, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<Ingredient>>(ingredients, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Return referenced ingredient */
	@Secured ("ROLE_ADMIN")
	@RequestMapping (value = "/ingredients/{id}", method = RequestMethod.GET)
	public ResponseEntity<Ingredient> getIngredient(@PathVariable ("id") String id) {
		Ingredient ingredient = null;
		try {
			ingredient = ingredientRepository.findOne(id);
			HttpStatus httpStatus = ingredient != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
			return new ResponseEntity<Ingredient>(ingredient, httpStatus);
		} catch (Exception e) {
			return new ResponseEntity<Ingredient>(ingredient, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Modify existing ingredient */
	@Secured ("ROLE_ADMIN")
	@RequestMapping (value = "/ingredients/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Ingredient> modifyIngredient
			(@PathVariable ("id") String id, @RequestBody Ingredient ingredient) {
		Ingredient modifiedIngredient = null;
		try {
			Ingredient currentIngredient = ingredientRepository.findOne(id);
			if(currentIngredient == null) {
				return new ResponseEntity<Ingredient>(modifiedIngredient, HttpStatus.NOT_FOUND);
			}
			if(!IngredientValidator.validateIngredient(ingredient)) {
				// Ingredient is not valid
				return new ResponseEntity<Ingredient>(modifiedIngredient, HttpStatus.NOT_ACCEPTABLE);
			}
			// Check if name is modified
			if(!ingredient.getName().equalsIgnoreCase(currentIngredient.getName())) {
				// Name has been modified
				if(ingredientRepository.findByNameIgnoreCase(ingredient.getName()).size() > 0) {
					// An ingredient with this name already exists
					return new ResponseEntity<Ingredient>(modifiedIngredient, HttpStatus.NOT_ACCEPTABLE);
				}
			}
			currentIngredient.updateIngredient(ingredient);
			modifiedIngredient = ingredientRepository.save(currentIngredient);
			return new ResponseEntity<Ingredient>(modifiedIngredient, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Ingredient>(modifiedIngredient, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Delete referenced ingredient */
	@Secured ("ROLE_ADMIN")
	@RequestMapping (value = "/ingredients/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Boolean> deleteIngredient (@PathVariable ("id") String id) {
		try {
			Ingredient currentIngredient = ingredientRepository.findOne(id);
			if(currentIngredient == null) {
				return new ResponseEntity<Boolean>(false, HttpStatus.NOT_FOUND);
			}
			ingredientRepository.delete(currentIngredient);
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Boolean>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Create new ingredient */
	@Secured ("ROLE_ADMIN")
	@RequestMapping (value = "/ingredients", method = RequestMethod.POST)
	public ResponseEntity<Ingredient> addIngredient
			(@RequestBody Ingredient ingredient) {
		Ingredient newIngredient = null;
		try {
			if(!IngredientValidator.validateIngredient(ingredient)) {
				return new ResponseEntity<Ingredient>(newIngredient,HttpStatus.NOT_ACCEPTABLE);
			}
			if(ingredientRepository.findByNameIgnoreCase(ingredient.getName()).size() > 0) {
				// An ingredient with this name already exists
				return new ResponseEntity<Ingredient>(newIngredient, HttpStatus.NOT_ACCEPTABLE);
			}
			newIngredient = ingredientRepository.save(ingredient);
			return new ResponseEntity<Ingredient>(newIngredient, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<Ingredient>(newIngredient, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	// CATEGORY HANDLER

	/* Return category list */
	@Secured ("ROLE_ADMIN")
	@RequestMapping (value = "/categories", method = RequestMethod.GET)
	public ResponseEntity<List<Category>> getCategories() {
		List<Category> categories = null;
		try {
			categories = (List<Category>) categoryRepository.findAll();
			return new ResponseEntity<List<Category>>(categories, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<Category>>(categories, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Return referenced category */
	@Secured ("ROLE_ADMIN")
	@RequestMapping (value = "/categories/{id}", method = RequestMethod.GET)
	public ResponseEntity<Category> getCategory(@PathVariable ("id") String id) {
		Category category = null;
		try {
			category = categoryRepository.findOne(id);
			HttpStatus httpStatus = category != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
			return new ResponseEntity<Category>(category, httpStatus);
		} catch (Exception e) {
			return new ResponseEntity<Category>(category, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Modify existing category */
	@Secured ("ROLE_ADMIN")
	@RequestMapping (value = "/categories/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Category> modifyCategory
			(@PathVariable ("id") String id, @RequestBody Category category) {
		Category modifiedCategory = null;
		try {
			Category currentCategory = categoryRepository.findOne(id);
			if(currentCategory == null) {
				return new ResponseEntity<Category>(modifiedCategory, HttpStatus.NOT_FOUND);
			}
			if(!CategoryValidator.validateCategory(category) || category.getName().trim().equalsIgnoreCase("")) {
				// Category is not valid
				return new ResponseEntity<Category>(modifiedCategory, HttpStatus.NOT_ACCEPTABLE);
			}
			if(!currentCategory.isFavorite() && category.isFavorite()) {
				if(categoryRepository.findByFavoriteTrue().size() >= BurgerJavisConstants.MAX_FAVORITES) {
					// Max number of favorites reached
					return new ResponseEntity<Category>(modifiedCategory, HttpStatus.NOT_ACCEPTABLE);
				}
			}
			// Check if name is modified
			if(!category.getName().equalsIgnoreCase(currentCategory.getName())) {
				// Name has been modified
				if(categoryRepository.findByNameIgnoreCase(category.getName()).size() > 0) {
					// An ingredient with this name already exists
					return new ResponseEntity<Category>(modifiedCategory, HttpStatus.NOT_ACCEPTABLE);
				}
			}
			currentCategory.updateCategory(category);
			modifiedCategory = categoryRepository.save(currentCategory);
			return new ResponseEntity<Category>(modifiedCategory, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Category>(modifiedCategory, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Delete referenced category */
	@Secured ("ROLE_ADMIN")
	@RequestMapping (value = "/categories/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Boolean> deleteCategory (@PathVariable ("id") String id) {
		try {
			Category currentCategory = categoryRepository.findOne(id);
			if(currentCategory == null) {
				return new ResponseEntity<Boolean>(false, HttpStatus.NOT_FOUND);
			}
			categoryRepository.delete(currentCategory);
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Boolean>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Create new category */
	@Secured ("ROLE_ADMIN")
	@RequestMapping (value = "/categories", method = RequestMethod.POST)
	public ResponseEntity<Category> addCategory(@RequestBody Category category) {
		Category newCategory = null;
		try {
			if(!CategoryValidator.validateCategory(category) || category.getName().trim().equalsIgnoreCase("")) {
				return new ResponseEntity<Category>(newCategory, HttpStatus.NOT_ACCEPTABLE);
			}
			if(categoryRepository.findByNameIgnoreCase(category.getName()).size() > 0) {
				// A category with this name already exists
				return new ResponseEntity<Category>(newCategory, HttpStatus.NOT_ACCEPTABLE);
			}
			newCategory = categoryRepository.save(category);
			return new ResponseEntity<Category>(newCategory, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<Category>(newCategory, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	// SUMMARY HANDLER

	/* Return summary data */
	@Secured ("ROLE_ADMIN")
	@RequestMapping (value = "/summary", method = RequestMethod.GET)
	public ResponseEntity<SummaryData> getSummaryData() {
		SummaryData data = null;
		try {
			List<Order> orders = orderRepository.findByFinishedTrue();
			List<Category> categories = (List<Category>) categoryRepository.findAll();
			if (orders.size() > 0) {
				data = new SummaryData(orders, categories);
				return new ResponseEntity<SummaryData>(data, HttpStatus.OK);
			}
			return new ResponseEntity<SummaryData>(data, HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<SummaryData>(data, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// USER HANDLER
	
	/* Return user list */
	@RequestMapping (value = "/users/{username}", method = RequestMethod.GET)
	public ResponseEntity <Credentials> getUsers(@PathVariable ("username") String username) {
		Credentials credentials = null;
		try {
			User user = userRepository.findByUsernameIgnoreCase(username);
			if (user == null) {
				return new ResponseEntity<Credentials>(credentials, HttpStatus.NOT_FOUND);
			}
			credentials = new Credentials(user);
			return new ResponseEntity<Credentials>(credentials, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Credentials>(credentials, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
