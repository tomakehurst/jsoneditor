package com.github.tomakehurst;

import java.util.NoSuchElementException;

import net.minidev.json.JSONObject;

public class ObjectEditor {
	
	private JsonEditor editor;
	private JSONObject object;
	
	public ObjectEditor(JsonEditor editor, JSONObject object) {
		this.editor = editor;
		this.object = object;
	}
	
	public <T> JsonEditor add(JSONAttribute<T> attribute) {
		if (attribute.getValue() instanceof CopySpec) {
			Object value = editor.find(((CopySpec) attribute.getValue()).getJsonPath());
			object.put(attribute.getKey(), value);
		} else {
			object.put(attribute.getKey(), attribute.getValue());
		}
		
		return editor;
	}
	
	public JsonEditor set(String key, String value) {
		if (!object.containsKey(key)) {
			throw new NoSuchElementException(key);
		}
		
		object.put(key, value);
		return editor;
	}
}
