/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.burgerjavis.Common;
import com.burgerjavis.Credentials;
import com.burgerjavis.entities.User;
import com.burgerjavis.repositories.UserRepository;

@CrossOrigin
@RestController
@RequestMapping("/appclient/users")
public class BurgerJavisRESTUser {
	
	// REQUIRED REPOSITORIES
	@Autowired
	private UserRepository userRepository;
	
	
	// USER HANDLER
	
	/* Return user list */
	@RequestMapping (value = "{username}", method = RequestMethod.GET)
	public ResponseEntity <Credentials> getUser(@PathVariable ("username") String username) {
		Credentials credentials = null;
		try {
			User user = userRepository.findByUsernameIgnoreCase(username);
			if (user == null) {
				return new ResponseEntity<Credentials>(credentials, HttpStatus.NOT_FOUND);
			}
			credentials = new Credentials(user);
			return new ResponseEntity<Credentials>(credentials, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Credentials>(credentials, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/* Return username list */
	@Secured ({Common.ADMIN_ROLE})
	@RequestMapping (value = "username", method = RequestMethod.GET)
	public ResponseEntity <List<String>> getUsernames() {
		List<String> usernames = new ArrayList<String>();
		try {
			for (User user : userRepository.findAll()) {
				if(user.isAdmin() || user.hasWaiterRole()) {
					usernames.add(user.getUsername());
				}
			}
			return new ResponseEntity<List<String>>(usernames, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<String>>(usernames, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
