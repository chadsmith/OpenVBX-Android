package org.openvbx;

public class User {
	private int id;
	private String first_name = null;
	private String last_name = null;

	public int getId() {
		return id;
	}

	public String getName() {
		return first_name + " " + last_name;
	}

	public User(int id, String first_name, String last_name) {
		this.id = id;
		this.first_name = first_name;
		this.last_name = last_name;
	}
}