package org.openvbx.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Inbox implements Parcelable {

    public int max = 20;
    public Boolean offset = false;
    public int total = 0;
    @SerializedName("folders")
    public ArrayList<Folder> folders = new ArrayList<>();

    protected Inbox(Parcel in) {
        max = in.readInt();
        byte offsetVal = in.readByte();
        offset = offsetVal == 0x02 ? null : offsetVal != 0x00;
        total = in.readInt();
        if (in.readByte() == 0x01) {
            folders = new ArrayList<>();
            in.readList(folders, Folder.class.getClassLoader());
        } else {
            folders = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(max);
        if (offset == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (offset ? 0x01 : 0x00));
        }
        dest.writeInt(total);
        if (folders == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(folders);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Inbox> CREATOR = new Parcelable.Creator<Inbox>() {
        @Override
        public Inbox createFromParcel(Parcel in) {
            return new Inbox(in);
        }

        @Override
        public Inbox[] newArray(int size) {
            return new Inbox[size];
        }
    };

}