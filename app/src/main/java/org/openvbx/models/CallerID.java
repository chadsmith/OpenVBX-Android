package org.openvbx.models;

import com.google.gson.annotations.SerializedName;

public class CallerID {

	public int flow_id;
	public String id;
	public String name;
	public String phone;
	public String phone_number;
	public String url;
	public String method;
	public String smsUrl;
	public String smsMethod;
	@SerializedName("capabilities")
	public Capabilities capabilities;
	public String voiceApplicationSid;
	public Boolean installed;

	public class Capabilities {

		public Boolean voice = false;
		public Boolean sms = false;
		public Boolean mms = false;

	}
}