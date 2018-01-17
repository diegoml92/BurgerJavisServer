/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.rest;

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

import com.burgerjavis.Common;
import com.burgerjavis.entities.Product;
import com.burgerjavis.repositories.ProductRepository;
import com.burgerjavis.validation.ProductValidator;


@RestController
@RequestMapping("/appclient/products")
public class BurgerJavisRESTProduct {
	
	// REQUIRED SERVICES
	@Autowired
	private ProductRepository productRepository;
	
	  
	// PRODUCT HANDLER

	/* Return product list */
	@Secured ({Common.WAITER_ROLE, Common.KITCHEN_ROLE, Common.ADMIN_ROLE})
	@RequestMapping (value = "", method = RequestMethod.GET)
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
	@Secured (Common.ADMIN_ROLE)
	@RequestMapping (value = "{id}", method = RequestMethod.GET)
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
	@Secured (Common.ADMIN_ROLE)
	@RequestMapping (value = "{id}", method = RequestMethod.PUT)
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
	@Secured (Common.ADMIN_ROLE)
	@RequestMapping (value = "{id}", method = RequestMethod.DELETE)
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
	@Secured (Common.ADMIN_ROLE)
	@RequestMapping (value = "", method = RequestMethod.POST)
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

}
