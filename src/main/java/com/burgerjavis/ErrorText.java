/**
 * Diego Moreno Lennon
 * Burger Javi's Server
 */

package com.burgerjavis;

public class ErrorText {
	
	public enum ErrorCause { NOT_FOUND, INVALID_DATA, MAX_FAVS, NAME_IN_USE, MIN_ADMINS }
	
	private String mainText;
	private ErrorCause causeText;
	
	public ErrorText(String mainText, ErrorCause causeText) {
		this.mainText = mainText;
		this.causeText = causeText;
	}

	public String getMainText() {
		return mainText;
	}

	public ErrorCause getCauseText() {
		return causeText;
	}

		

}
