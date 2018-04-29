/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.rest;

import java.security.Principal;
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
import com.burgerjavis.repositories.UserRepository;
import com.burgerjavis.validation.OrderValidator;


@RestController
@RequestMapping("/appclient/orders")
public class BurgerJavisRESTOrder {
	
	// REQUIRED SERVICES
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private UserRepository userRepository;
	
	  
	// ORDER HANDLER

	/* Return order list */
	@Secured ({Common.WAITER_ROLE, Common.ADMIN_ROLE})
	@RequestMapping (value = "", method = RequestMethod.GET)
	public ResponseEntity<List<Order>> getOrders(Principal principal) {
		List<Order> orders = null;
		try {
			if(!userRepository.findByUsernameIgnoreCase(principal.getName()).isAdmin()) {
				orders = (List<Order>) orderRepository.
					findByUsernameIgnoreCaseAndStateIsNot(principal.getName(), OrderState.FINISHED);
			} else {
				orders = (List<Order>) orderRepository.findByStateIsNot(OrderState.FINISHED);
			}
			return new ResponseEntity<List<Order>>(orders, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<Order>>(orders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Return referenced order */
	@Secured ({Common.WAITER_ROLE, Common.ADMIN_ROLE})
	@RequestMapping (value = "{id}", method = RequestMethod.GET)
	public ResponseEntity<Order> getOrder(@PathVariable ("id") String id, Principal principal) {
		Order order = null;
		try {
			order = orderRepository.findOne(id);
			if(order == null) {
				return new ResponseEntity<Order>(order, HttpStatus.NOT_FOUND);
			}
			if(!order.getUsername().equalsIgnoreCase(principal.getName()) &&
					!userRepository.findByUsernameIgnoreCase(principal.getName()).isAdmin()) {
				return new ResponseEntity<Order>(order, HttpStatus.UNAUTHORIZED);
			}
			if(order.isState(OrderState.FINISHED)) {
				return new ResponseEntity<Order>(order, HttpStatus.FORBIDDEN);
			}
			return new ResponseEntity<Order>(order, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Order>(order, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Modify existing order */
	@Secured ({Common.WAITER_ROLE, Common.ADMIN_ROLE})
	@RequestMapping (value = "{id}", method = RequestMethod.PUT)
	public ResponseEntity<Order> modifyOrder
			(@PathVariable ("id") String id, @RequestBody Order order, Principal principal) {
		Order modifiedOrder = null;
		try {
			Order currentOrder = orderRepository.findOne(id);
			if(currentOrder == null) {
				// Order not found
				return new ResponseEntity<Order>(modifiedOrder, HttpStatus.NOT_FOUND);
			}
			if(!currentOrder.getUsername().equalsIgnoreCase(principal.getName()) &&
					!userRepository.findByUsernameIgnoreCase(principal.getName()).isAdmin()) {
				// Unauthorized user
				return new ResponseEntity<Order>(modifiedOrder, HttpStatus.UNAUTHORIZED);
			}
			if(!(currentOrder.isState(OrderState.INITIAL) || currentOrder.isState(OrderState.SERVED))) {
				return new ResponseEntity<Order>(modifiedOrder, HttpStatus.FORBIDDEN);
			}
			if(!OrderValidator.validateOrder(order)) {
				// Order not valid
				return new ResponseEntity<Order>(modifiedOrder, HttpStatus.NOT_ACCEPTABLE);
			}
			if(currentOrder.isState(OrderState.SERVED)) {
				order = currentOrder;
				order.setState(OrderState.FINISHED);
			}else if(!order.getName().equalsIgnoreCase(currentOrder.getName())) {
				// Name has been modified, check if new name is available
				List<Order> conflictingOrders = orderRepository.findByNameIgnoreCase(order.getName());
				boolean conflict = false;
				int i=0;
				while (!conflict && i<conflictingOrders.size()) {
					conflict = !conflictingOrders.get(i).isState(OrderState.FINISHED);
					i++;
				}
				if (conflict) {
					// New name is already being used
					return new ResponseEntity<Order>(modifiedOrder, HttpStatus.NOT_ACCEPTABLE);
				}
			}
			currentOrder.updateOrder(order);
			modifiedOrder = orderRepository.save(currentOrder);
			return new ResponseEntity<Order>(modifiedOrder, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Order>(modifiedOrder, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Delete referenced order */
	@Secured ({Common.WAITER_ROLE, Common.ADMIN_ROLE})
	@RequestMapping (value = "{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Boolean> deleteOrder (@PathVariable ("id") String id, Principal principal) {
		try {
			Order currentOrder = orderRepository.findOne(id);
			if(currentOrder == null) {
				return new ResponseEntity<Boolean>(false, HttpStatus.NOT_FOUND);
			}
			if(!currentOrder.getUsername().equalsIgnoreCase(principal.getName()) &&
					!userRepository.findByUsernameIgnoreCase(principal.getName()).isAdmin()) {
				// Unauthorized user
				return new ResponseEntity<Boolean>(false, HttpStatus.UNAUTHORIZED);
			}
			if(currentOrder.isState(OrderState.FINISHED)) {
				return new ResponseEntity<Boolean>(false, HttpStatus.FORBIDDEN);
			}
			orderRepository.delete(currentOrder);
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Boolean>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/* Create new order */
	@Secured ({Common.WAITER_ROLE, Common.ADMIN_ROLE})
	@RequestMapping (value = "", method = RequestMethod.POST)
	public ResponseEntity<Order> addOrder(@RequestBody Order order, Principal principal) {
		Order newOrder = null;
		try {
			if(!OrderValidator.validateOrder(order)) {
				// Order not valid
				return new ResponseEntity<Order>(newOrder, HttpStatus.NOT_ACCEPTABLE);
			}
			if(!order.getUsername().equalsIgnoreCase(principal.getName())) {
				// Incoherent data
				return new ResponseEntity<Order>(newOrder, HttpStatus.NOT_ACCEPTABLE);
			}
			List<Order> orders = orderRepository.findByNameIgnoreCase(order.getName());
			// Order name can be reused if previous order with same name is finished
			boolean create = true;
			if(orders.size() > 0) {
				int i = 0;
				while(create && i < orders.size()) {
					create = orders.get(i).isState(OrderState.FINISHED);
					i++;
				}
			}
			if (!create) {
				return new ResponseEntity<Order>(newOrder, HttpStatus.NOT_ACCEPTABLE);
			}
			newOrder = orderRepository.save(order);
			return new ResponseEntity<Order>(newOrder, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<Order>(newOrder, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
