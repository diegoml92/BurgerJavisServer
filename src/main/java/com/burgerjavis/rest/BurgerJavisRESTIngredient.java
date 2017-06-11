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

import com.burgerjavis.entities.Ingredient;
import com.burgerjavis.repositories.IngredientRepository;
import com.burgerjavis.validation.IngredientValidator;


@RestController
@RequestMapping("/appclient/ingredients")
public class BurgerJavisRESTIngredient {
	
	// REQUIRED REPOSITORIES
	@Autowired
	private IngredientRepository ingredientRepository;
	
	
	// INGREDIENT HANDLER

	/* Return ingredients list */
	@Secured ("ROLE_ADMIN")
	@RequestMapping (value = "", method = RequestMethod.GET)
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
	@RequestMapping (value = "{id}", method = RequestMethod.GET)
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
	@RequestMapping (value = "{id}", method = RequestMethod.PUT)
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
	@RequestMapping (value = "{id}", method = RequestMethod.DELETE)
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
	@RequestMapping (value = "", method = RequestMethod.POST)
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

}
