/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.burgerjavis.Credentials;
import com.burgerjavis.entities.User;
import com.burgerjavis.repositories.UserRepository;


@RestController
@RequestMapping("/appclient/users")
public class BurgerJavisRESTUser {
	
	// REQUIRED REPOSITORIES
	@Autowired
	private UserRepository userRepository;
	
	
	// USER HANDLER
	
	/* Return user list */
	@RequestMapping (value = "{username}", method = RequestMethod.GET)
	public ResponseEntity <Credentials> getUsers(@PathVariable ("username") String username) {
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

}
