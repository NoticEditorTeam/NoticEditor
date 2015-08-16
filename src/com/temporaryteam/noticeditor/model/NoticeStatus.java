/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.temporaryteam.noticeditor.model;

/**
 *
 * @author Maximillian M.
 */
public class NoticeStatus {
	private String name;
	private int code;
	
	public NoticeStatus(String name, int code) {
		this.name = name;
		this.code = code;
	}
			
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String value) {
		name = value;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
