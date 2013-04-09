package org.openvbx;

public class Folder {
	private int id;
	private String name = null;
	private String type = null;
	private int unread = 0;

	public int getId(){
		return id;
	}

	public String getName(){
		return name;
	}

	public String getType() {
		return type;
	}

	public int getUnread() {
		return unread;
	}

	public Folder(int id, String name, String type, int unread) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.unread = unread;
	}
}