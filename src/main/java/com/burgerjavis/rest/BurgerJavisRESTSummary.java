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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.burgerjavis.Common.OrderState;
import com.burgerjavis.entities.Category;
import com.burgerjavis.entities.Order;
import com.burgerjavis.repositories.CategoryRepository;
import com.burgerjavis.repositories.OrderRepository;
import com.burgerjavis.summary.SummaryData;


@RestController
@RequestMapping("/appclient/summary")
public class BurgerJavisRESTSummary {
	
	// REQUIRED REPOSITORIES
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private CategoryRepository categoryRepository;


	// SUMMARY HANDLER

	/* Return summary data */
	@Secured ("ROLE_ADMIN")
	@RequestMapping (value = "", method = RequestMethod.GET)
	public ResponseEntity<SummaryData> getSummaryData() {
		SummaryData data = null;
		try {
			List<Order> orders = orderRepository.findByStateIs(OrderState.FINISHED);
			List<Category> categories = (List<Category>) categoryRepository.findAll();
			if (orders.size() > 0) {
				data = new SummaryData(orders, categories);
				return new ResponseEntity<SummaryData>(data, HttpStatus.OK);
			}
			return new ResponseEntity<SummaryData>(data, HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<SummaryData>(data, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
