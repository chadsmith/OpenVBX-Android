package org.openvbx.models;


import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Message {

	public int id;
    public Boolean selected_folder = false;
    public Boolean selected_folder_id = false;
    public String status = "read";
	public String folder = "";
    public int folder_id;
    public String summary = "";
    public String short_summary = "";
    public int assigned = -1;
    public String type = "";
    public int assigned_user;
    public String ticket_status;
    public Boolean archived = false;
    public Boolean unread = false;
    public String recording_url;
    public String recording_length;
    public String received_time;
    public String last_updated;
    public String called;
    public String caller;
    public String original_called;
    public String original_caller;
    public String owner_type;
    public String message_type;
    @SerializedName("active_users")
    public ArrayList<User> active_users = new ArrayList<>();
    @SerializedName("annotations")
    public Annotations annotations = new Annotations();

    public String receivedTime() {
        try {
            return new SimpleDateFormat("M/d/yy h:mm a").format(new SimpleDateFormat("yyyy-MMM-dd'T'HH:mm:ssZ").parse(received_time));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public class Annotations {

        @SerializedName("items")
        public ArrayList<Annotation> items = new ArrayList<>();
        public int max = 0;
        public int total = 0;

    }

}