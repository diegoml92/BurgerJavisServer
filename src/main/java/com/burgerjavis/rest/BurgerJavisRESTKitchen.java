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
import com.burgerjavis.Common.OrderState;
import com.burgerjavis.entities.Order;
import com.burgerjavis.repositories.OrderRepository;


@RestController
@RequestMapping("/appclient/kitchen")
public class BurgerJavisRESTKitchen {
	
	// REQUIRED REPOSITORIES
	@Autowired
	private OrderRepository orderRepository;

	
	// KITCHEN HANDLER
	
	/* Return order list */
	@Secured ({Common.KITCHEN_ROLE, Common.ADMIN_ROLE})
	@RequestMapping (value = "", method = RequestMethod.GET)
	public ResponseEntity<List<Order>> getKitchenOrders() {
		List<Order> orders = null;
		try {
			orders = (List<Order>) orderRepository.findByStateIs(OrderState.KITCHEN);
			return new ResponseEntity<List<Order>>(orders, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<Order>>(orders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/* Return referenced order */
	@Secured ({Common.KITCHEN_ROLE, Common.ADMIN_ROLE})
	@RequestMapping (value = "{id}", method = RequestMethod.GET)
	public ResponseEntity<Order> getKitchenOrder(@PathVariable ("id") String id) {
		Order order = null;
		try {
			order = orderRepository.findOne(id);
			if(order == null) {
				return new ResponseEntity<Order>(order, HttpStatus.NOT_FOUND);
			}
			if(!order.isState(OrderState.KITCHEN)) {
				return new ResponseEntity<Order>(order, HttpStatus.FORBIDDEN);
			}
			return new ResponseEntity<Order>(order, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Order>(order, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/* Modify existing order */
	@Secured ({Common.KITCHEN_ROLE, Common.ADMIN_ROLE})
	@RequestMapping (value = "{id}", method = RequestMethod.PUT)
	public ResponseEntity<Order> modifyKitchenOrder (@PathVariable ("id") String id, @RequestBody Order order) {
		Order modifiedOrder = null;
		try {
			Order currentOrder = orderRepository.findOne(id);
			if(currentOrder == null) {
				// Order not found
				return new ResponseEntity<Order>(modifiedOrder, HttpStatus.NOT_FOUND);
			}
			if(!currentOrder.isState(OrderState.KITCHEN)) {
				return new ResponseEntity<Order>(modifiedOrder, HttpStatus.FORBIDDEN);
			}
			currentOrder.setState(OrderState.SERVED);
			modifiedOrder = orderRepository.save(currentOrder);
			return new ResponseEntity<Order>(modifiedOrder, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Order>(modifiedOrder, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
