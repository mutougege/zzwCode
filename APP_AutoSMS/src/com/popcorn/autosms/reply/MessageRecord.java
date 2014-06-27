package com.popcorn.autosms.reply;

import java.util.Date;

public class MessageRecord {
	private String person;
	private String address;
	private String time;
	public void setTime(String time) {
		this.time = time;
	}
	private String body;
	private int id;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public MessageRecord() {
		super();
	}
	public String getPerson() {
		return person;
	}
	public void setPerson(String person) {
		this.person = person;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTime() {
		return time;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}

	
}
