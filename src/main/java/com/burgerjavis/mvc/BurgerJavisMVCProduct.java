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
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView addProduct () {
		return new ModelAndView("to_do_template");
	}
	
	@RequestMapping (value = "/get{id}", method = RequestMethod.GET)
	public ModelAndView getProduct(String id) {
		Product product = productRepository.findOne(id);
		List<Category> categories = (List<Category>) categoryRepository.findAll();
		return new ModelAndView("edit_product").addObject("product", product).addObject("categories", categories);

	}
	
	@RequestMapping (value= "/modify{id}", method = RequestMethod.PUT)
	public ModelAndView modifyProduct(String id, Product product) {
		final String errorText = "ERROR ACTUALIZANDO PRODUCTO";
		Product currentProduct = productRepository.findOne(id);
		if(currentProduct == null) {
			ErrorCause cause = ErrorCause.NOT_FOUND;
			return new ModelAndView("edit_product").addObject("product", product).
					addObject("error", new ErrorText(errorText, cause));
		}
		if(!ProductValidator.validateProduct(product)) {
			ErrorCause cause = ErrorCause.INVALID_DATA;
			return new ModelAndView("edit_product").addObject("product", currentProduct).
					addObject("error", new ErrorText(errorText, cause));
		}
		// Check if name is modified
		if(!product.getName().equalsIgnoreCase(currentProduct.getName())) {
			// Name has been modified
			if(productRepository.findByNameIgnoreCase(product.getName()).size() > 0) {
				ErrorCause cause = ErrorCause.NAME_IN_USE;
				return new ModelAndView("edit_product").addObject("product", currentProduct).
						addObject("error", new ErrorText(errorText, cause));
			}
		}
		currentProduct.updateProduct(product);
		productRepository.save(currentProduct);
		return new ModelAndView("redirect:/");
	}
}
