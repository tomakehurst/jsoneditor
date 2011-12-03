package com.github.tomakehurst;

import net.minidev.json.JSONArray;

public interface ArrayItemTransformer<S, T> {

	T transform(S item, int index, JSONArray array);
}
