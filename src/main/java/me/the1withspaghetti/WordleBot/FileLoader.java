package me.the1withspaghetti.WordleBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class FileLoader {
	
	public static final Gson gson = new Gson();
	
	
	public static JsonArray loadJsonFromFile(String file) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		return gson.fromJson(new FileReader(new File(file)), JsonArray.class);
	}
	
	public static JsonArray loadJsonFromResource(String resource) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		return loadJsonFromStream(FileLoader.class.getResourceAsStream(resource));
	}
	
	public static JsonArray loadJsonFromStream(InputStream in) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		return gson.fromJson(new InputStreamReader(in), JsonArray.class);
	}
	
	
	public static HashSet<char[]> toHashSet(JsonArray json) {
		HashSet<char[]> words = new HashSet<>();
		json.iterator().forEachRemaining((i) -> {
			char[] array = i.getAsString().toCharArray();
			if (array.length != 5) System.err.println("Warning: "+i.getAsString()+" is not a 5 letter word!");
			words.add(array);
		});
		return words;
	}
	
	public static LinkedHashSet<char[]> toLinkedHashSet(JsonArray json) {
		LinkedHashSet<char[]> words = new LinkedHashSet<>();
		json.iterator().forEachRemaining((i) -> {
			char[] array = i.getAsString().toCharArray();
			if (array.length != 5) System.err.println("Warning: "+i.getAsString()+" is not a 5 letter word!");
			words.add(array);
		});
		return words;
	}
	
	public static LinkedList<char[]> toLinkedList(JsonArray json) {
		LinkedList<char[]> words = new LinkedList<>();
		json.iterator().forEachRemaining((i) -> {
			char[] array = i.getAsString().toCharArray();
			if (array.length != 5) System.err.println("Warning: "+i.getAsString()+" is not a 5 letter word!");
			words.add(array);
		});
		return words;
	}
}
