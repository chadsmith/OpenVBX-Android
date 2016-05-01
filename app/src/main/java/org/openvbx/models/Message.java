package org.openvbx.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Message implements Parcelable {

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
            return new SimpleDateFormat("M/d/yy h:mm a", Locale.getDefault()).format(new SimpleDateFormat("yyyy-MMM-dd'T'HH:mm:ssZ", Locale.getDefault()).parse(received_time));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean isVoice() {
        return type != null && type.equals("voice");
    }

    public Boolean isSms() {
        return type != null && type.equals("sms");
    }

    protected Message(Parcel in) {
        id = in.readInt();
        byte selected_folderVal = in.readByte();
        selected_folder = selected_folderVal == 0x02 ? null : selected_folderVal != 0x00;
        byte selected_folder_idVal = in.readByte();
        selected_folder_id = selected_folder_idVal == 0x02 ? null : selected_folder_idVal != 0x00;
        status = in.readString();
        folder = in.readString();
        folder_id = in.readInt();
        summary = in.readString();
        short_summary = in.readString();
        assigned = in.readInt();
        type = in.readString();
        assigned_user = in.readInt();
        ticket_status = in.readString();
        byte archivedVal = in.readByte();
        archived = archivedVal == 0x02 ? null : archivedVal != 0x00;
        byte unreadVal = in.readByte();
        unread = unreadVal == 0x02 ? null : unreadVal != 0x00;
        recording_url = in.readString();
        recording_length = in.readString();
        received_time = in.readString();
        last_updated = in.readString();
        called = in.readString();
        caller = in.readString();
        original_called = in.readString();
        original_caller = in.readString();
        owner_type = in.readString();
        message_type = in.readString();
        if (in.readByte() == 0x01) {
            active_users = new ArrayList<>();
            in.readList(active_users, User.class.getClassLoader());
        } else {
            active_users = null;
        }
        annotations = (Annotations) in.readValue(Annotations.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        if (selected_folder == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (selected_folder ? 0x01 : 0x00));
        }
        if (selected_folder_id == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (selected_folder_id ? 0x01 : 0x00));
        }
        dest.writeString(status);
        dest.writeString(folder);
        dest.writeInt(folder_id);
        dest.writeString(summary);
        dest.writeString(short_summary);
        dest.writeInt(assigned);
        dest.writeString(type);
        dest.writeInt(assigned_user);
        dest.writeString(ticket_status);
        if (archived == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (archived ? 0x01 : 0x00));
        }
        if (unread == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (unread ? 0x01 : 0x00));
        }
        dest.writeString(recording_url);
        dest.writeString(recording_length);
        dest.writeString(received_time);
        dest.writeString(last_updated);
        dest.writeString(called);
        dest.writeString(caller);
        dest.writeString(original_called);
        dest.writeString(original_caller);
        dest.writeString(owner_type);
        dest.writeString(message_type);
        if (active_users == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(active_users);
        }
        dest.writeValue(annotations);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public static class Annotations implements Parcelable {

        @SerializedName("items")
        public ArrayList<Annotation> items = new ArrayList<>();
        public int max = 0;
        public int total = 0;

        public Annotations() {
        }

        protected Annotations(Parcel in) {
            if (in.readByte() == 0x01) {
                items = new ArrayList<>();
                in.readList(items, Annotation.class.getClassLoader());
            } else {
                items = null;
            }
            max = in.readInt();
            total = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (items == null) {
                dest.writeByte((byte) (0x00));
            } else {
                dest.writeByte((byte) (0x01));
                dest.writeList(items);
            }
            dest.writeInt(max);
            dest.writeInt(total);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<Annotations> CREATOR = new Parcelable.Creator<Annotations>() {
            @Override
            public Annotations createFromParcel(Parcel in) {
                return new Annotations(in);
            }

            @Override
            public Annotations[] newArray(int size) {
                return new Annotations[size];
            }
        };

    }

}