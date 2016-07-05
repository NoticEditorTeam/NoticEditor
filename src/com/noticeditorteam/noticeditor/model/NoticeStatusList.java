package com.noticeditorteam.noticeditor.model;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Collection of notice statuses
 *
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

	/**
	 * Adds new status with next status code
	 *
	 * @param statusName Status name
	 */
	public static void add(String statusName) {
		++lastIndex;
		NoticeStatus newStatus = new NoticeStatus(statusName, lastIndex);
		list.add(newStatus);
	}

	/**
	 * Adds new status with specified status code
	 *
	 * @param statusName Status name
	 * @param statusCode Status code
	 */
	public static void add(String statusName, int statusCode) {
		lastIndex = (lastIndex < statusCode) ? statusCode : lastIndex + 1;
		NoticeStatus newStatus = new NoticeStatus(statusName, statusCode);
		list.add(newStatus);
	}

	/**
	 * Clears status list
	 */
	public static void clear() {
		list = new ArrayList<>();
	}

	/**
	 * Returns status by code
	 *
	 * @param statusCode Status code
	 * @return Status
	 */
	public static NoticeStatus getStatus(int statusCode) {
		NoticeStatus foo = list.stream().filter(status -> status.getCode() == statusCode).findFirst().orElse(list.get(0));
		return foo;
	}

	/**
	 * Returns status code by name
	 *
	 * @param statusName Status name
	 * @return Status code
	 */
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
