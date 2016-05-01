package org.openvbx.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Annotation implements Parcelable {

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

	protected Annotation(Parcel in) {
		id = in.readInt();
		annotation_type = in.readString();
		message_id = in.readInt();
        user_id = in.readInt();
		description = in.readString();
		created = in.readString();
		tenant_id = in.readInt();
		email = in.readString();
		first_name = in.readString();
		last_name = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(annotation_type);
		dest.writeInt(message_id);
		dest.writeInt(user_id);
		dest.writeString(description);
		dest.writeString(created);
		dest.writeInt(tenant_id);
		dest.writeString(email);
		dest.writeString(first_name);
		dest.writeString(last_name);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Annotation> CREATOR = new Parcelable.Creator<Annotation>() {
		@Override
		public Annotation createFromParcel(Parcel in) {
			return new Annotation(in);
		}

		@Override
		public Annotation[] newArray(int size) {
			return new Annotation[size];
		}
	};

}