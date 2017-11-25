/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis;

public class Common {
	
	public static final int MAX_FAVORITES = 3;
	public static final int MIN_ADMINS = 1;
	public static final int MIN_PASS_LENGTH = 4;
	
	public final static String REALM = "BURGER_JAVIS_REALM";
	
	public final static String WAITER_ROLE  = "ROLE_WAITER";
	public final static String KITCHEN_ROLE = "ROLE_KITCHEN";
	public final static String ADMIN_ROLE   = "ROLE_ADMIN";
	
	public enum OrderState {INITIAL, KITCHEN, SERVED, FINISHED};
	
}
