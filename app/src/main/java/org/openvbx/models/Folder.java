package org.openvbx.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Folder implements Parcelable {

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

    public Folder() {
    }

	protected Folder(Parcel in) {
		id = in.readInt();
		name = in.readString();
		type = in.readString();
		archived = in.readInt();
		unread = in.readInt();
		read = in.readInt();
		total = in.readInt();
		messages = (Messages) in.readValue(Messages.class.getClassLoader());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(type);
		dest.writeInt(archived);
		dest.writeInt(unread);
		dest.writeInt(read);
		dest.writeInt(total);
		dest.writeValue(messages);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Folder> CREATOR = new Parcelable.Creator<Folder>() {
		@Override
		public Folder createFromParcel(Parcel in) {
			return new Folder(in);
		}

		@Override
		public Folder[] newArray(int size) {
			return new Folder[size];
		}
	};

	public static class Messages implements Parcelable {

		@SerializedName("items")
		public ArrayList<Message> items = new ArrayList<>();
        public int total = 0;
        public Boolean offset = false;
        public int max = 20;

		public Messages() {
		}

		protected Messages(Parcel in) {
			if (in.readByte() == 0x01) {
				items = new ArrayList<>();
				in.readList(items, Message.class.getClassLoader());
			} else {
				items = null;
			}
			total = in.readInt();
			byte offsetVal = in.readByte();
			offset = offsetVal == 0x02 ? null : offsetVal != 0x00;
			max = in.readInt();
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
			dest.writeInt(total);
			if (offset == null) {
				dest.writeByte((byte) (0x02));
			} else {
				dest.writeByte((byte) (offset ? 0x01 : 0x00));
			}
			dest.writeInt(max);
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<Messages> CREATOR = new Parcelable.Creator<Messages>() {
			@Override
			public Messages createFromParcel(Parcel in) {
				return new Messages(in);
			}

			@Override
			public Messages[] newArray(int size) {
				return new Messages[size];
			}
		};

	}

}