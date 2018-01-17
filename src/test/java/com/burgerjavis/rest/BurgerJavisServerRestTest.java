/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis.rest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	BurgerJavisServerRestOrderTest.class,
	BurgerJavisServerRestProductTest.class,
	BurgerJavisServerRestIngredientTest.class,
	BurgerJavisServerRestCategoryTest.class,
	BurgerJavisServerRestKitchenTest.class,
	BurgerJavisServerRestSummaryTest.class,
	BurgerJavisServerRestUserTest.class
})
public class BurgerJavisServerRestTest {}
