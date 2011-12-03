package com.github.tomakehurst;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Arrays.asList;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import com.google.common.io.CharStreams;
import com.jayway.jsonpath.JsonPath;

public class JsonEditor {

	private Object root;
	
	public static JsonEditor edit(String filePath) throws IOException, ParseException {
		JsonEditor builder = new JsonEditor();
		builder.root = new JSONParser(JSONParser.MODE_PERMISSIVE).parse(CharStreams.toString(new FileReader(filePath)));
		return builder;
	}
	
	public ObjectEditor object(String jsonPath) {
		Object jsonElement = JsonPath.read(root, jsonPath);
		if (!(jsonElement instanceof JSONObject)) {
			throw new IllegalArgumentException("Element referred to by " + jsonPath + " is not an object");
		}
		
		return new ObjectEditor(this, (JSONObject) jsonElement);
	}
	
	public ArrayEditor array(String jsonPath) {
		Object jsonElement = JsonPath.read(root, jsonPath);
		if (!(jsonElement instanceof JSONArray)) {
			throw new IllegalArgumentException("Element referred to by " + jsonPath + " is not an array");
		}
		
		return new ArrayEditor(this, (JSONArray) jsonElement);
	}
	
	public static <T> JSONArray newArray(T... items) {
		JSONArray array = new JSONArray();
		array.addAll(asList(items));
		return array;
	}
	
	public static JSONObject newObject(JSONAttribute<Object>... attributes) {
		JSONObject object = new JSONObject();
		object.putAll(asMap(attributes));
		return object;
	}
	
	private static <T> Map<String, T> asMap(JSONAttribute<T>... attributes) {
		Map<String, T> map = newHashMap();
		for (JSONAttribute<T> attribute: attributes) {
			map.put((String) attribute.getKey(), attribute.getValue());
		}
		
		return map;
	}
	
	
	public static <T> JSONAttribute<Object> newAttribute(String key, T value) {
		return new JSONAttribute<Object>(key, value);
	}
	
	public String asString() {
		return root.toString();
	}
	
	public String toString() {
		return asString();
	}
	
}
