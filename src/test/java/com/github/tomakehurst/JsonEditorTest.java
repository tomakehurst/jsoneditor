package com.github.tomakehurst;

import static com.github.tomakehurst.JsonEditor.copyOf;
import static com.github.tomakehurst.JsonEditor.json;
import static com.github.tomakehurst.JsonEditor.newArray;
import static com.github.tomakehurst.JsonEditor.newAttribute;
import static com.github.tomakehurst.JsonEditor.newObject;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.FileReader;
import java.util.NoSuchElementException;

import net.minidev.json.JSONArray;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.io.CharStreams;

public class JsonEditorTest {

	private static final String EXAMPLE_JSON_FILE = "src/test/resources/simple-example.json";
	
	private TestJson json;
	
	@Before
	public void init() throws Exception {
		json = readJsonFromFile(EXAMPLE_JSON_FILE);
	}

	@Test
	public void canUpdateAnAttribute() throws Exception {
		assertThat(json.getString("$.one.two.attribute"), is("Old two value"));
		
		TestJson newJson = new TestJson(
			JsonEditor.edit(EXAMPLE_JSON_FILE)
			.object("$.one.two").set("attribute", "New two value")
			.asString());
		
		assertThat(newJson.getString("$.one.two.attribute"), is("New two value"));
	}
	
	@Test(expected=NoSuchElementException.class)
	public void throwsExceptionWhenAttemptingToSetNonExistentAttribute() throws Exception {
		JsonEditor.edit(EXAMPLE_JSON_FILE)
			.object("$.one.two").set("nonAttribute", "New value");
	}
	
	@Test
	public void canAddAnAttribute() throws Exception {
		String editedJson = 
			JsonEditor.edit(EXAMPLE_JSON_FILE)
			.object("$.one").add(newAttribute("four", 4))
			.asString();
		
		TestJson json = new TestJson(editedJson);
		assertThat(json.getInt("$.one.four"), is(4));
	}
	
	String meaningOfLifeJson = 
		"{ 								\n" +
		"	\"meaning\": {				\n" +
		"		\"of\": {				\n" +
		"			\"life\": 42		\n" +
		"		}						\n" +
		"	}							\n" +
		"} 								";
	
	@Test
	public void canAddAnAttributeFromJsonLiteral() throws Exception {
		String editedJson = 
			JsonEditor.edit(EXAMPLE_JSON_FILE)
			.object("$.one").add(newAttribute("four", json(meaningOfLifeJson)))
			.asString();
		
		System.out.println(editedJson);
		TestJson json = new TestJson(editedJson);
		assertThat(json.getInt("$.one.four.meaning.of.life"), is(42));
	}
	
	@Test
	public void canAddValueToArray() throws Exception {
		TestJson newJson = new TestJson(
				JsonEditor.edit(EXAMPLE_JSON_FILE)
				.array("$.one.two.array").add("New item")
				.asString());
		
		assertThat(newJson.getString("$.one.two.array[4]"), is("New item"));
	}
	
	@Test
	public void canInsertValueIntoArray() throws Exception {
		String editedJson = 
			JsonEditor.edit(EXAMPLE_JSON_FILE)
			.array("$.one.two.array").insert(2, "New item")
			.asString();

		TestJson testJson = new TestJson(editedJson);
		assertThat(testJson.getString("$.one.two.array[2]"), is("New item"));
	}
	
	@Test
	public void canInsertCopyIntoArray() throws Exception {
		String editedJson = 
			JsonEditor.edit(EXAMPLE_JSON_FILE)
			.array("$.one.two.array").insert(0, copyOf("$.one.three"))
			.asString();

		TestJson testJson = new TestJson(editedJson);
		assertThat(testJson.getString("$.one.two.array[0].attribute"), is("Three value"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void canAddObjectToArray() throws Exception {
		String editedJson =
			JsonEditor.edit(EXAMPLE_JSON_FILE)
				.array("$.one.two.array").add(
						newObject(
								newAttribute("name", "Bob"),
								newAttribute("age", 31)))
				.asString();
		
		TestJson testJson = new TestJson(editedJson);
		assertThat(testJson.getString("$.one.two.array[4].name"), is("Bob"));
		assertThat(testJson.getInt("$.one.two.array[4].age"), is(31));
	}
	
	@Test
	public void canAddCopiedObjectToArray() throws Exception {
		String editedJson =
			JsonEditor.edit(EXAMPLE_JSON_FILE)
				.array("$.one.two.array").add(
						copyOf("$.one.three"))
				.asString();
		
		TestJson testJson = new TestJson(editedJson);
		assertThat(testJson.getString("$.one.two.array[4].attribute"), is("Three value"));
	}
	
	@Test
	public void canAddArrayToArray() throws Exception {
		String editedJson =
			JsonEditor.edit(EXAMPLE_JSON_FILE)
				.array("$.one.two.array").add(
						newArray(1, 1, 2, 3, 5, 8))
				.asString();
		
		TestJson testJson = new TestJson(editedJson);
		assertThat(testJson.getInt("$.one.two.array[4][0]"), is(1));
		assertThat(testJson.getInt("$.one.two.array[4][3]"), is(3));
		assertThat(testJson.getInt("$.one.two.array[4][5]"), is(8));
	}
	
	@Test
	public void canRemoveItemsFromArrayByRange() throws Exception {
		assertThat(json.count("$.one.two.array"), is(4));
		
		TestJson newJson = new TestJson(
				JsonEditor.edit(EXAMPLE_JSON_FILE)
				.array("$.one.two.array").remove(1, 2)
				.asString());
		
		assertThat(newJson.count("$.one.two.array"), is(2));
		assertThat(newJson.getString("$.one.two.array[0]"), is("item 1"));
		assertThat(newJson.getString("$.one.two.array[1]"), is("item 4"));
	}
	
	@Test
	public void canRemoveItemsFromArrayByPredicate() throws Exception {
		assertThat(json.count("$.one.two.array"), is(4));
		
		TestJson newJson = new TestJson(
				JsonEditor.edit(EXAMPLE_JSON_FILE)
				.array("$.one.two.array").remove(ifIsItem1Or2())
				.asString());
		
		assertThat(newJson.count("$.one.two.array"), is(2));
		assertThat(newJson.getString("$.one.two.array[0]"), is("item 3"));
		assertThat(newJson.getString("$.one.two.array[1]"), is("item 4"));
	}
	
	private Predicate<String> ifIsItem1Or2() {
		return new Predicate<String>() {
			public boolean apply(String input) {
				return input.equals("item 1") || input.equals("item 2");
			}
			
		};
	}
	
	@Test
	public void canTransformItemsInArrayWithFunction() throws Exception {
		String editedJson =
			JsonEditor.edit(EXAMPLE_JSON_FILE)
				.array("$.one.two.array").transform(changeValue())
				.asString();
		
		TestJson testJson = new TestJson(editedJson);
		assertThat(testJson.getString("$.one.two.array[0]"), is("item 1 transformed"));
		assertThat(testJson.getString("$.one.two.array[2]"), is("item 3 transformed"));
	}
	
	private Function<String, String> changeValue() {
		return new Function<String, String>() {
			public String apply(String input) {
				return input + " transformed";
			}
		};
	}
	
	@Test
	public void canTransformItemsInArrayWithTransformer() throws Exception {
		String editedJson =
			JsonEditor.edit(EXAMPLE_JSON_FILE)
				.array("$.one.two.array").transform(concatenatePrevious())
				.asString();
		
		TestJson testJson = new TestJson(editedJson);
		assertThat(testJson.getString("$.one.two.array[0]"), is("item 1 item 4"));
		assertThat(testJson.getString("$.one.two.array[2]"), is("item 3 item 2"));
	}
	
	@Test
	public void canCopyFromOnePartToAnother() throws Exception {
		String editedJson =
			JsonEditor.edit(EXAMPLE_JSON_FILE)
				.object("$.one").add(newAttribute("four", copyOf("$.one.three")))
				.asString();
		
		System.out.println(editedJson);
		TestJson testJson = new TestJson(editedJson);
		assertThat(testJson.getString("$.one.four.attribute"), is("Three value"));
	}
	
	private ArrayItemTransformer<String, String> concatenatePrevious() {
		return new ArrayItemTransformer<String, String>() {
			public String transform(String item, int index, JSONArray array) {
				int prevIndex = index == 0 ? array.size() - 1 : index - 1;
				return item + " " + array.get(prevIndex);
			}
		};
	}
	
	private TestJson readJsonFromFile(String filePath) throws Exception {
		String json = CharStreams.toString(new FileReader(EXAMPLE_JSON_FILE));
		return new TestJson(json);
	}
}
