package com.burgerjavis.mvc.wrappers;

public interface Wrapper<T> {
	
	public T getInternalType();
	
	public void wrapInternalType(T param);

}
