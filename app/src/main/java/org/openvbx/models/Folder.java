package org.openvbx.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Folder {

	public int id = -1;
	public String name = null;
	public String type = null;
	public int archived = 0;
	@SerializedName("new")
	public int unread = 0;
	public int read = 0;
	public int total = 0;
	@SerializedName("messages")
	public Messages messages = new Messages();

	public class Messages {

		@SerializedName("items")
		public ArrayList<Message> items = new ArrayList<>();
        public int total = 0;
        public Boolean offset = false;
        public int max = 20;

	}

}