/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.burgerjavis.BurgerJavisConstants;
import com.burgerjavis.ErrorText;
import com.burgerjavis.ErrorText.ErrorCause;
import com.burgerjavis.entities.Category;
import com.burgerjavis.repositories.CategoryRepository;
import com.burgerjavis.validation.CategoryValidator;

@Controller
@RequestMapping("/webclient/category**")
public class BurgerJavisMVCCategory {
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@RequestMapping (value = "/get{id}", method = RequestMethod.GET)
	public ModelAndView getCategory(String id) {
		Category category = categoryRepository.findOne(id);
		return new ModelAndView("edit_category").addObject("category", category);

	}
	
	@RequestMapping (value= "/modify{id}", method = RequestMethod.PUT)
	public ModelAndView modifyCategory (String id, Category category) {
		final String errorText = "ERROR ACTUALIZANDO CATEGORÍA";
		Category currentCategory = categoryRepository.findOne(id);
		if(currentCategory == null) {
			ErrorCause cause = ErrorCause.NOT_FOUND;
			return new ModelAndView("edit_category").addObject("category", category).
					addObject("error", new ErrorText(errorText, cause));
		}
		if(!CategoryValidator.validateCategory(category) || category.getName().trim().equalsIgnoreCase("")) {
			ErrorCause cause = ErrorCause.INVALID_DATA;
			return new ModelAndView("edit_category").addObject("category", currentCategory).
					addObject("error", new ErrorText(errorText, cause));
		}
		if(!currentCategory.isFavorite() && category.isFavorite()) {
			if(categoryRepository.findByFavoriteTrue().size() >= BurgerJavisConstants.MAX_FAVORITES) {
				ErrorCause cause = ErrorCause.MAX_FAVS;
				return new ModelAndView("edit_category").addObject("category", currentCategory).
						addObject("error", new ErrorText(errorText, cause));
			}
		}
		// Check if name is modified
		if(!category.getName().equalsIgnoreCase(currentCategory.getName())) {
			// Name has been modified
			if(categoryRepository.findByNameIgnoreCase(category.getName()).size() > 0) {
				ErrorCause cause = ErrorCause.NAME_IN_USE;
				return new ModelAndView("edit_category").addObject("category", currentCategory).
						addObject("error", new ErrorText(errorText, cause));
			}
		}
		currentCategory.updateCategory(category);
		categoryRepository.save(currentCategory);
		return new ModelAndView("redirect:/");
	}
	
}
