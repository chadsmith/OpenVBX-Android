package org.openvbx;

public class Annotation {
	private int id;
	private String first_name = null;
	private String last_name = null;
	private String description = null;

	public int getId() {
		return id;
	}

	public String getName() {
		return first_name + " " + last_name;
	}

	public String getDescription() {
		return description;
	}

	public Annotation(int id, String first_name, String last_name, String description) {
		this.id = id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.description = description;
	}
}