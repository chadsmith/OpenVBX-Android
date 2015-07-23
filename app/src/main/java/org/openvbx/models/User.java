package org.openvbx.models;

public class User {

	public int id;
    public String first_name;
    public String last_name;
    public String email;

	public String getName() {
		return first_name + " " + last_name;
	}

    public User(int id, String first_name, String last_name) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
    }

}