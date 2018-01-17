/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.burgerjavis.mvc.BurgerJavisServerMvcTest;
import com.burgerjavis.rest.BurgerJavisServerRestTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	BurgerJavisServerRestTest.class,
	BurgerJavisServerMvcTest.class
})
public class BurgerJavisServerControllerTest {}
