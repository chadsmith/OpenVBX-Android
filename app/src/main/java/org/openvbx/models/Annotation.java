package org.openvbx.models;

public class Annotation {

	public int id;
	public String annotation_type = "noted";
	public int message_id;
	public int user_id;
	public String description = "";
	public String created;
	public int tenant_id;
	public String email;
	public String first_name = "";
	public String last_name = "";

	public String getName() {
		return first_name + " " + last_name;
	}

}