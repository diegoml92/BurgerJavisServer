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
import com.burgerjavis.entities.Category;
import com.burgerjavis.repositories.CategoryRepository;
import com.burgerjavis.validation.CategoryValidator;


@RestController
@RequestMapping("/appclient/categories")
public class BurgerJavisRESTCategory {
	
	// REQUIRED REPOSITORIES
	@Autowired
	private CategoryRepository categoryRepository;

	// CATEGORY HANDLER

	/* Return category list */
	@Secured (Common.ADMIN_ROLE)
	@RequestMapping (value = "", method = RequestMethod.GET)
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
	@Secured (Common.ADMIN_ROLE)
	@RequestMapping (value = "{id}", method = RequestMethod.GET)
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
	@Secured (Common.ADMIN_ROLE)
	@RequestMapping (value = "{id}", method = RequestMethod.PUT)
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
				if(categoryRepository.findByFavoriteTrue().size() >= Common.MAX_FAVORITES) {
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
	@Secured (Common.ADMIN_ROLE)
	@RequestMapping (value = "{id}", method = RequestMethod.DELETE)
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
	@Secured (Common.ADMIN_ROLE)
	@RequestMapping (value = "", method = RequestMethod.POST)
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

}
