package com.github.tomakehurst;

import java.util.AbstractMap;

public class JSONAttribute<T> extends AbstractMap.SimpleEntry<String, T> {

	private static final long serialVersionUID = 1706398999116624639L;

	public JSONAttribute(String key, T value) {
		super(key, value);
	}
}
