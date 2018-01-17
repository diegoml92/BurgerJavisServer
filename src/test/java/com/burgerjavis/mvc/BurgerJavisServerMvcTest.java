/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.mvc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	BurgerJavisMVCLoginTest.class,
	BurgerJavisMVCHomeTest.class,
	BurgerJavisMVCCategoryTest.class,
	BurgerJavisMVCIngredientTest.class,
	BurgerJavisMVCProductTest.class,
	BurgerJavisMVCUserTest.class
})
public class BurgerJavisServerMvcTest {}
