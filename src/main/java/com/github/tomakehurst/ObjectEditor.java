package com.github.tomakehurst;

import net.minidev.json.JSONObject;

public class ObjectEditor {
	
	private JsonEditor editor;
	private JSONObject object;
	
	public ObjectEditor(JsonEditor editor, JSONObject object) {
		this.editor = editor;
		this.object = object;
	}

	public JsonEditor set(String key, String value) {
		object.put(key, value);
		return editor;
	}
}
