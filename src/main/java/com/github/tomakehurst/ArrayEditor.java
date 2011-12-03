package com.github.tomakehurst;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.removeIf;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import net.minidev.json.JSONArray;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class ArrayEditor {

	private JsonEditor editor;
	private JSONArray array;
	
	public ArrayEditor(JsonEditor editor, JSONArray array) {
		this.editor = editor;
		this.array = array;
	}
	
	public <T> JsonEditor add(T... items) {
		for (T item: items) {
			array.add(getItemOrFetchCopy(item));
		}
		
		return editor;
	}
	
	public <T> JsonEditor insert(int index, T item) {
		array.add(index, getItemOrFetchCopy(item));
		return editor;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getItemOrFetchCopy(T item) {
		if (item instanceof CopySpec) {
			return (T) editor.find(((CopySpec) item).getJsonPath());
		}
		
		return item;
	}
	
	public JsonEditor remove(int startIndex, int endIndex) {
		List<Object> objectsToRemove = newArrayList();
		for (int i = startIndex; i <= endIndex; i++) {
			objectsToRemove.add(array.get(i));
		}
		
		array.removeAll(objectsToRemove);
		return editor;
	}
	
	@SuppressWarnings("unchecked")
	public <T> JsonEditor remove(Predicate<T> predicate) {
		removeIf((Iterable<T>) array, predicate);
		return editor;
	}
	
	@SuppressWarnings("unchecked")
	public <S, T> JsonEditor transform(Function<S, T> function) {
		for (int i = 0; i < array.size(); i++) {
			T newValue = function.apply((S) array.get(i));
			array.set(i, newValue);
		}
		
		return editor;
	}
	
	@SuppressWarnings("unchecked")
	public <S, T> JsonEditor transform(ArrayItemTransformer<S, T> transformer) {
		JSONArray arrayCopy = new JSONArray();
		arrayCopy.addAll(copyOf(array));
		for (int i = 0; i < array.size(); i++) {
			T newValue = transformer.transform((S) array.get(i), i, arrayCopy);
			array.set(i, newValue);
		}
		
		return editor;
	}
}
