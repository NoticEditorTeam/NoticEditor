package com.temporaryteam.noticeditor.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * Collection of notice statuses
 * @author Maximillian M.
 */
public class NoticeStatus {
	private static final HashMap<String, Integer> map;
	private static int lastIndex;
	
	static {
		map = new HashMap<>();
		lastIndex = -1;
	}
	
	/**
	 * Associates the specified status with next status code
	 * @param status Status name
	 */
	public static void add(String status) {
		lastIndex++;
		map.put(status, lastIndex);
	}
	
	/**
	 * Associates the specified status with the specified status code
	 * @param status Status name
	 * @param code Status code
	 */
	public static void add(String status, int code) {
		if (lastIndex < code) {
			lastIndex = code;
		}
		map.put(status, code);
	}
	
	/**
	 * Removes the specified status
	 * @param status 
	 */
	public static void remove(String status) {
		map.remove(status);
	}
	
	/**
	 * Return status code by status name
	 * @param status Status name
	 * @return Status code if status exists, otherwise returns 0
	 */
	public static int getStatusCode(String status) {
		return map.containsKey(status) ? map.get(status) : 0;
	}
	
	/** 
	 * Returns status name by status code
	 * @param code Status code
	 * @return Status name if status exists, otherwise returns empty string
	 */
	public static String getStatusName(int code) {
		List<String> foo =  map.entrySet().stream()
				.filter(entry -> entry.getValue() == code)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
		if (foo.isEmpty()) {
			// TODO: May be Status[0]?
			return "";
		} else {
			return foo.get(0);
		}
	}
	
	/**
	 * Clears collection of notice statuses
	 */
	public static void clear() {
		map.clear();
	}
	
	public static ObservableList<String> asObservable() {
		return FXCollections.observableArrayList(map.keySet());
	}
	
	public static ObservableMap<String, Integer> asObservableMap() {
		return FXCollections.observableMap(map);
	}
}
