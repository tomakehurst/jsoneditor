package com.github.tomakehurst;

import java.text.ParseException;

import net.minidev.json.JSONArray;

import com.jayway.jsonpath.JsonPath;

public class TestJson {

	private String jsonSource;

	public TestJson(String jsonSource) {
		this.jsonSource = jsonSource;
	}
	
	public String getString(String pathSpec) throws ParseException {
		return JsonPath.read(jsonSource, pathSpec);
	}
	
	public int getInt(String pathSpec) throws ParseException {
		return JsonPath.read(jsonSource, pathSpec);
	}
	
	public int count(String pathSpec) throws ParseException {
		return ((JSONArray) JsonPath.read(jsonSource, pathSpec)).size();
	}
	
	public String toString() {
		return jsonSource;
	}
}
