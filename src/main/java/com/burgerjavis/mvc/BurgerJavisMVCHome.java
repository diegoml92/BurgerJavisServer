/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.burgerjavis.Common.OrderState;
import com.burgerjavis.entities.Category;
import com.burgerjavis.entities.Order;
import com.burgerjavis.entities.User;
import com.burgerjavis.repositories.CategoryRepository;
import com.burgerjavis.repositories.OrderRepository;
import com.burgerjavis.repositories.UserRepository;
import com.burgerjavis.summary.SummaryData;

@Controller
public class BurgerJavisMVCHome {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	
	@RequestMapping ({"/", "/webclient/home"})
	public ModelAndView loginPage() {
		List<User> users  = (List<User>) userRepository.findAll();
		SummaryData data = new SummaryData();
		List<Order> orders = orderRepository.findByStateIs(OrderState.FINISHED);
		List<Category> categories = (List<Category>) categoryRepository.findAll();
		if (orders.size() > 0) {
			data = new SummaryData(orders, categories);
		}
		return new ModelAndView("index").addObject("users", users).addObject("data", data);
	} 

}
