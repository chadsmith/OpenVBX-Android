package org.openvbx.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CallerID implements Parcelable {

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
	public Capabilities capabilities = new Capabilities();
	public String voiceApplicationSid;
	public Boolean installed;

    public Boolean hasSms() {
        return capabilities != null && capabilities.sms;
    }

    protected CallerID(Parcel in) {
        flow_id = in.readInt();
        id = in.readString();
        name = in.readString();
        phone = in.readString();
        phone_number = in.readString();
        url = in.readString();
        method = in.readString();
        smsUrl = in.readString();
        smsMethod = in.readString();
        capabilities = (Capabilities) in.readValue(Capabilities.class.getClassLoader());
        voiceApplicationSid = in.readString();
        byte installedVal = in.readByte();
        installed = installedVal == 0x02 ? null : installedVal != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(flow_id);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(phone_number);
        dest.writeString(url);
        dest.writeString(method);
        dest.writeString(smsUrl);
        dest.writeString(smsMethod);
        dest.writeValue(capabilities);
        dest.writeString(voiceApplicationSid);
        if (installed == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (installed ? 0x01 : 0x00));
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CallerID> CREATOR = new Parcelable.Creator<CallerID>() {
        @Override
        public CallerID createFromParcel(Parcel in) {
            return new CallerID(in);
        }

        @Override
        public CallerID[] newArray(int size) {
            return new CallerID[size];
        }
    };

	public static class Capabilities implements Parcelable {

		public Boolean voice = false;
		public Boolean sms = false;
		public Boolean mms = false;

		public Capabilities() {
		}

        protected Capabilities(Parcel in) {
            byte voiceVal = in.readByte();
            voice = voiceVal == 0x02 ? null : voiceVal != 0x00;
            byte smsVal = in.readByte();
            sms = smsVal == 0x02 ? null : smsVal != 0x00;
            byte mmsVal = in.readByte();
            mms = mmsVal == 0x02 ? null : mmsVal != 0x00;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (voice == null) {
                dest.writeByte((byte) (0x02));
            } else {
                dest.writeByte((byte) (voice ? 0x01 : 0x00));
            }
            if (sms == null) {
                dest.writeByte((byte) (0x02));
            } else {
                dest.writeByte((byte) (sms ? 0x01 : 0x00));
            }
            if (mms == null) {
                dest.writeByte((byte) (0x02));
            } else {
                dest.writeByte((byte) (mms ? 0x01 : 0x00));
            }
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<Capabilities> CREATOR = new Parcelable.Creator<Capabilities>() {
            @Override
            public Capabilities createFromParcel(Parcel in) {
                return new Capabilities(in);
            }

            @Override
            public Capabilities[] newArray(int size) {
                return new Capabilities[size];
            }
        };

	}
}