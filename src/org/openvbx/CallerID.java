package org.openvbx;

public class CallerID {
	private String phone_number = null;
	private String name = null;
	private Boolean sms = false;

	public String getNumber() {
		return phone_number;
	}

	public String getName() {
		return name;
	}

	public Boolean isSMS() {
		return sms;
	}

	public CallerID(String phone_number, String name, Boolean sms) {
		this.phone_number = phone_number;
		this.name = name;
		this.sms = sms;
	}
}