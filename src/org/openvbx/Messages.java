package org.openvbx;


public class Messages {
	private int id;
	private String folder = null;
	private String type = null;
	private String caller = null;
	private String short_summary = null;
	private Boolean unread = false;
	private String received_time = null;

	public int getId() {
		return id;
	}

	public String getFolder() {
		return folder;
	}

	public String getType() {
		return type;
	}

	public String getCaller() {
		return caller;
	}

	public String getSummary() {
		return short_summary;
	}

	public Boolean isUnread() {
		return unread;
	}

	public String getTime() {
		return received_time;
	}

	public Messages(int id, String folder, String type, String caller, String short_summary, Boolean unread, String received_time) {
		this.id = id;
		this.folder = folder;
		this.type = type;
		this.caller = caller;
		this.short_summary = short_summary;
		this.unread = unread;
		this.received_time = received_time;
	}
}