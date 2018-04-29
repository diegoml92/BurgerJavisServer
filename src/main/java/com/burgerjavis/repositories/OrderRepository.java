/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.burgerjavis.Common.OrderState;
import com.burgerjavis.entities.Order;

public interface OrderRepository extends CrudRepository<Order, String> {
	
	public List<Order> findByNameIgnoreCase (String name);
	
	public List<Order> findByStateIsNot (OrderState state);
	
	public List<Order> findByUsernameIgnoreCaseAndStateIsNot (String username, OrderState state);
	
	public List<Order> findByUsernameIgnoreCaseAndStateIs (String username, OrderState state);
	
	public List<Order> findByStateIs (OrderState state);
		
}
