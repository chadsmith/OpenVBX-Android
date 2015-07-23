package org.openvbx.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Inbox {

    public int max = 20;
    public Boolean offset = false;
    public int total = 0;
    @SerializedName("folders")
    public ArrayList<Folder> folders = new ArrayList<>();

}