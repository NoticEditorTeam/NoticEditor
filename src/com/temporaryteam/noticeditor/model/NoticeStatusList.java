/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.temporaryteam.noticeditor.model;

import java.util.ArrayList;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Collection of notice statuses
 * @author Maximillian M.
 */
public class NoticeStatusList {
	private static ArrayList<NoticeStatus> list;
	private static int lastIndex;
	
	private static ArrayList<NoticeStatus> mementoList;
	private static int mementoLastIndex;
	
	static {
		list = new ArrayList<>();
		lastIndex = -1;
	}
	
	/**
	 * Saves status list
	 */
	public static void save() {
		mementoList = new ArrayList<>(list);
		mementoLastIndex = lastIndex;
	}
	
	/**
	 * Restores statuses to saved state
	 */
	public static void restore() {
		list = mementoList;
		lastIndex = mementoLastIndex;
	}
	
	public static void add(String statusName) {
		++lastIndex;
		NoticeStatus newStatus = new NoticeStatus(statusName, lastIndex);
		list.add(newStatus);
	}
	
	public static void add(String statusName, int statusCode) {
		lastIndex = (lastIndex < statusCode) ? statusCode : lastIndex + 1;
		NoticeStatus newStatus = new NoticeStatus(statusName, statusCode);
		list.add(newStatus);
	}
	
	public static void clear() {
		list = new ArrayList<>();
	}
	
	public static NoticeStatus getStatus(int statusCode) {
		NoticeStatus foo = list.stream().filter(status -> status.getCode() == statusCode).findFirst().orElse(list.get(0));
		return foo;
	}
	
	public static int getStatusCode(String statusName) {
		for (NoticeStatus status : list) {
			if (status.getName().equals(statusName)) {
				return status.getCode();
			}
		}
		return 0;
	}
	
	public static ObservableList<NoticeStatus> asObservable() {
		return FXCollections.observableList(list);
	}
}
