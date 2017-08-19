/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BurgerJavisMVCHome {
	
	@RequestMapping ({"/", "/webclient/home"})
	public ModelAndView loginPage() {
		return new ModelAndView("index");
	} 

}
