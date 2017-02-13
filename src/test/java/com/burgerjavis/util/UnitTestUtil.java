package com.burgerjavis.util;

import java.nio.charset.Charset;

import org.springframework.http.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UnitTestUtil {
	
	public static final MediaType APPLICATION_JSON_UTF8 =
		new MediaType (MediaType.APPLICATION_JSON.getType(),
				MediaType.APPLICATION_JSON.getSubtype(),
				Charset.forName("utf8"));
	
	public static final double DELTA_ERROR = 0.0001;
	
	public static String  convertObjectToJson (Object object) {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(object);
	}
	
}
