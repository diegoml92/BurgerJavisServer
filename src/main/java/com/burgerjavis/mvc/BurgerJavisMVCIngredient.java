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

import com.burgerjavis.ErrorText;
import com.burgerjavis.ErrorText.ErrorCause;
import com.burgerjavis.entities.Ingredient;
import com.burgerjavis.repositories.IngredientRepository;
import com.burgerjavis.validation.IngredientValidator;

@Controller
@RequestMapping("/webclient/ingredient**")
public class BurgerJavisMVCIngredient {
	
	@Autowired
	IngredientRepository ingredientRepository;
	
	@RequestMapping (value = "/add", method = RequestMethod.GET)
	public ModelAndView addIngredient() {
		Ingredient ingredient = new Ingredient();
		return new ModelAndView("add_ingredient").addObject("ingredient", ingredient);
	}
	
	@RequestMapping (value = "/add", method = RequestMethod.POST)
	public ModelAndView addIngredient(Ingredient ingredient) {
		final String errorText = "ERROR CREANDO INGREDIENTE";
		if(!IngredientValidator.validateIngredient(ingredient)) {
			ErrorCause cause = ErrorCause.INVALID_DATA;
			return new ModelAndView("add_ingredient").addObject("ingredient", ingredient).
					addObject("error", new ErrorText(errorText, cause));
		}
		if(ingredientRepository.findByNameIgnoreCase(ingredient.getName()).size() > 0) {
			ErrorCause cause = ErrorCause.NAME_IN_USE;
			return new ModelAndView("add_ingredient").addObject("ingredient", ingredient).
					addObject("error", new ErrorText(errorText, cause));
		}
		ingredientRepository.save(ingredient);
		return new ModelAndView("redirect:/");
	}
	
	@RequestMapping (value = "/get{id}", method = RequestMethod.GET)
	public ModelAndView getIngredient(String id) {
		Ingredient ingredient = ingredientRepository.findOne(id);
		return new ModelAndView("edit_ingredient").addObject("ingredient", ingredient);
	}
	
	@RequestMapping (value= "/modify{id}", method = RequestMethod.PUT)
	public ModelAndView modifyIngredient (String id, Ingredient ingredient) {
		final String errorText = "ERROR ACTUALIZANDO INGREDIENTE";
		Ingredient currentIngredient = ingredientRepository.findOne(id);
		if(currentIngredient == null) {
			ErrorCause cause = ErrorCause.NOT_FOUND;
			return new ModelAndView("edit_ingredient").addObject("ingredient", ingredient).
					addObject("error", new ErrorText(errorText, cause));
		}
		if(!IngredientValidator.validateIngredient(ingredient)) {
			ErrorCause cause = ErrorCause.INVALID_DATA;
			return new ModelAndView("edit_ingredient").addObject("ingredient", currentIngredient).
					addObject("error", new ErrorText(errorText, cause));
		}
		// Check if name is modified
		ModelAndView result = checkNameModified(ingredient, currentIngredient, errorText);
		if(result != null) {
			return result;
		}
		currentIngredient.updateIngredient(ingredient);
		ingredientRepository.save(currentIngredient);
		return new ModelAndView("redirect:/");
	}
	
	@RequestMapping (value= "/delete{id}", method = RequestMethod.DELETE)
	public ModelAndView deleteIngredient (String id) {
		final String errorText = "ERROR BORRANDO INGREDIENTE";
		Ingredient currentIngredient = ingredientRepository.findOne(id);
		if(currentIngredient == null) {
			ErrorCause cause = ErrorCause.NOT_FOUND;
			return new ModelAndView("edit_ingredient").addObject("ingredient", currentIngredient).
					addObject("error", new ErrorText(errorText, cause));
		}
		ingredientRepository.delete(currentIngredient);
		return new ModelAndView("redirect:/");
	}
	
	private ModelAndView checkNameModified(Ingredient ingredient, Ingredient currentIngredient, String errorText) {
		if(!ingredient.getName().equalsIgnoreCase(currentIngredient.getName())) {
			// Name has been modified
			if(ingredientRepository.findByNameIgnoreCase(ingredient.getName()).size() > 0) {
				ErrorCause cause = ErrorCause.NAME_IN_USE;
				return new ModelAndView("edit_ingredient").addObject("ingredient", currentIngredient).
						addObject("error", new ErrorText(errorText, cause));
			}
		}
		return null;
	}
}
