package com.github.tomakehurst;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import org.junit.Test;

import com.jayway.jsonpath.JsonPath;

public class JsonPathTest {

	private static final String JSON_SAMPLE =
		"{ 													\n" +
		"	\"one\": {										\n" +
		"		\"attribute\": \"One-value\",				\n" +
		"		\"array\": [								\n" +
		"			\"Array value 1\",						\n" +
		"			\"Array value 2\",						\n" +
		"			\"Array value 3\"						\n" +
		"		]											\n" +
		"	},												\n" +
		"	\"two\": {										\n" +
		"		\"attribute\": \"Two-value\"				\n" +
		"	}												\n" +
		"}													";
	
	@Test
	public void readingArrays() throws Exception {
		Object root = parse(JSON_SAMPLE);
		JSONArray array = JsonPath.read(root, "$.one.array");
		assertThat(array.size(), is(3));
	}
	
	@Test
	public void readingObjects() throws Exception {
		Object root = parse(JSON_SAMPLE);
		JSONObject object = JsonPath.read(root, "$.one");
		assertThat((String) object.get("attribute"), is("One-value"));
	}
	
	@Test
	public void updatingValues() throws Exception {
		Object root = parse(JSON_SAMPLE);
		JSONObject object = JsonPath.read(root, "$.one");
		object.put("attribute", "New two-value");
		String resultingJson = root.toString();
		System.out.println(resultingJson);
	}
	
	@Test
	public void readingMultipleObjects() throws Exception {
		Object root = parse(JSON_SAMPLE);
		Object object = JsonPath.read(root, "$..attribute");
		JSONArray array = (JSONArray) object;
		System.out.println(array.get(0).getClass());
	}
	
	private Object parse(String json) throws Exception {
		return new JSONParser(JSONParser.MODE_PERMISSIVE).parse(json);
	}
}
