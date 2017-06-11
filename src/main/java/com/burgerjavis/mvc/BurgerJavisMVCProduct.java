/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/webclient/products")
public class BurgerJavisMVCProduct {
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView addProduct () {
		return new ModelAndView("to_do_template");
	}
}
