package org.openvbx;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.widget.Toast;

public class OpenVBXApplication extends Application {

	private SharedPreferences settings;
	private SharedPreferences.Editor editor;

	private String server = null;
	private String email = null;
	private String password = null;
	private String device = null;

	public ProgressDialog dialog = null;

	private ArrayList<CallerID> callerids = null;

	public void onCreate() {
		settings = getSharedPreferences("settings", MODE_PRIVATE);
		server = settings.getString("server", null);
		email = settings.getString("email", null);
		password = settings.getString("password", null);
		device = settings.getString("device", null);
	}

	public String getServer() {
		return server;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getDevice() {
		return device;
	}

	public ArrayList<CallerID> getCallerIDs(String type) {
		if("voice".equals(type))
			return this.callerids;
		ArrayList<CallerID> res = new ArrayList<CallerID>();
		for(int i = 0; i < this.callerids.size(); i++)
			if(this.callerids.get(i).isSMS())
				res.add(this.callerids.get(i));
		return res;
	}

	public void setServer(String server) {
		this.server = server;
		editor = settings.edit();
		editor.putString("server", server);
		editor.commit();
	}

	public void setLoginCredentials(String email, String password) {
		this.email = email;
		this.password = password;
		editor = settings.edit();
		editor.putString("email", email);
		editor.putString("password", password);
		editor.commit();
	}

	public void setDevice(String device) {
		this.device = device;
		editor = settings.edit();
		editor.putString("device", device);
		editor.commit();
	}

	public void setCallerIDs(ArrayList<CallerID> callerids) {
		this.callerids = callerids;
	}

	public void alert(Context context, String title, String message) {
		dismissDialog();
		AlertDialog alert = new AlertDialog.Builder(context).create();
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
	      public void onClick(DialogInterface dialog, int which) {
	        return;
	      }
	    }); 
		alert.show();
	}

	public void dialog(Context context, String message) {
		dialog = new ProgressDialog(context);
		dialog.setMessage(message);
		dialog.show();
	}

	public void dismissDialog() {
		if(dialog != null && dialog.isShowing())
			dialog.dismiss();
	}

	public void status(Context context, String text) {
		dismissDialog();
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.show();
	}
}