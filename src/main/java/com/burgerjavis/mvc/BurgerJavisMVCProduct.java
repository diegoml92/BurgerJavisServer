/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.burgerjavis.ErrorText;
import com.burgerjavis.ErrorText.ErrorCause;
import com.burgerjavis.entities.Category;
import com.burgerjavis.entities.Product;
import com.burgerjavis.mvc.wrappers.ProductWrapper;
import com.burgerjavis.repositories.CategoryRepository;
import com.burgerjavis.repositories.ProductRepository;
import com.burgerjavis.validation.ProductValidator;

@Controller
@RequestMapping("/webclient/product**")
public class BurgerJavisMVCProduct {
	
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	
	@RequestMapping (value = "/add", method = RequestMethod.GET)
	public ModelAndView addProduct() {
		ProductWrapper productWrapper = new ProductWrapper();
		List<Category> categories = (List<Category>) categoryRepository.findAll();
		return new ModelAndView("add_product").addObject("product", productWrapper).addObject("categories", categories);
	}
	
	@RequestMapping (value = "/add", method = RequestMethod.POST)
	public ModelAndView addProduct(ProductWrapper product) {
		final String errorText = "ERROR CREANDO PRODUCTO";
		Product newProduct = product.getInternalType();
		List<Category> categories = (List<Category>) categoryRepository.findAll();
		if(!ProductValidator.validateProduct(newProduct)) {
			ErrorCause cause = ErrorCause.INVALID_DATA;
			return new ModelAndView("add_product").addObject("product", product).addObject("categories", categories).
					addObject("error", new ErrorText(errorText, cause));
		}
		if(productRepository.findByNameIgnoreCase(newProduct.getName()).size() > 0) {
			ErrorCause cause = ErrorCause.NAME_IN_USE;
			return new ModelAndView("add_product").addObject("product", product).addObject("categories", categories).
					addObject("error", new ErrorText(errorText, cause));
		}
		productRepository.save(newProduct);
		return new ModelAndView("redirect:/");
	}

	
	@RequestMapping (value = "/get{id}", method = RequestMethod.GET)
	public ModelAndView getProduct(String id) {
		Product product = productRepository.findOne(id);
		ProductWrapper productWrapper = new ProductWrapper();
		productWrapper.wrapInternalType(product);
		List<Category> categories = (List<Category>) categoryRepository.findAll();
		return new ModelAndView("edit_product").addObject("product", productWrapper).addObject("categories", categories);
	}
	
	@RequestMapping (value= "/modify{id}", method = RequestMethod.PUT)
	public ModelAndView modifyProduct(String id, ProductWrapper product) {
		final String errorText = "ERROR ACTUALIZANDO PRODUCTO";
		Product modProduct = product.getInternalType();
		Product currentProduct = productRepository.findOne(id);
		List<Category> categories = (List<Category>) categoryRepository.findAll();
		if(currentProduct == null) {
			ErrorCause cause = ErrorCause.NOT_FOUND;
			return new ModelAndView("edit_product").addObject("product", product).addObject("categories", categories).
					addObject("error", new ErrorText(errorText, cause));
		}
		if(!ProductValidator.validateProduct(modProduct)) {
			ErrorCause cause = ErrorCause.INVALID_DATA;
			ProductWrapper productWrapper = new ProductWrapper();
			productWrapper.wrapInternalType(currentProduct);
			return new ModelAndView("edit_product").addObject("product", productWrapper).addObject("categories", categories).
					addObject("error", new ErrorText(errorText, cause));
		}
		// Check if name is modified
		if(!modProduct.getName().equalsIgnoreCase(currentProduct.getName())) {
			// Name has been modified
			if(productRepository.findByNameIgnoreCase(modProduct.getName()).size() > 0) {
				ErrorCause cause = ErrorCause.NAME_IN_USE;
				ProductWrapper productWrapper = new ProductWrapper();
				productWrapper.wrapInternalType(currentProduct);
				return new ModelAndView("edit_product").addObject("product", productWrapper).addObject("categories", categories).
						addObject("error", new ErrorText(errorText, cause));
			}
		}
		currentProduct.updateProduct(modProduct);
		productRepository.save(currentProduct);
		return new ModelAndView("redirect:/");
	}
	
	@RequestMapping (value= "/delete{id}", method = RequestMethod.DELETE)
	public ModelAndView deleteProduct (String id) {
		final String errorText = "ERROR BORRANDO PRODUCTO";
		Product currentProduct = productRepository.findOne(id);
		if(currentProduct == null) {
			ErrorCause cause = ErrorCause.NOT_FOUND;
			ProductWrapper productWrapper = new ProductWrapper();
			productWrapper.wrapInternalType(currentProduct);
			List<Category> categories = (List<Category>) categoryRepository.findAll();
			return new ModelAndView("edit_product").addObject("product", productWrapper).addObject("categories", categories).
					addObject("error", new ErrorText(errorText, cause));
		}
		productRepository.delete(currentProduct);
		return new ModelAndView("redirect:/");
	}
}
