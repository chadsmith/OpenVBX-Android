package org.openvbx;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class sms extends Activity {

	private OpenVBXApplication OpenVBX;
	
	private CallerIDAdapter adapter;
	private Spinner spinner;
	private Context context = this;
	
	private String from = null;
	private String to = null;
	private int message_id = 0;
	private String content = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms);
        OpenVBX = (OpenVBXApplication) getApplication();
        spinner = (Spinner) findViewById(R.id.from);
        adapter = new CallerIDAdapter(this, OpenVBX.getCallerIDs("sms"));
        spinner.setAdapter(adapter);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
        	spinner.setSelection(adapter.indexOf(extras.getString("from")));
        	((EditText) findViewById(R.id.to)).setText(extras.getString("to"));
        	message_id = extras.getInt("message_id");
        }
		((Button) findViewById(R.id.send)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (hasValidInput()) {
					OpenVBX.dialog(context, "Sending...");
					AsyncHttpClient client = new AsyncHttpClient();
					client.addHeader("Accept", "application/json");
					client.setBasicAuth(OpenVBX.getEmail(), OpenVBX.getPassword());
					RequestParams params = new RequestParams();
					params.put("from", from);
					params.put("to", to);
					params.put("content", content);
					client.post(OpenVBX.getServer() + "/messages/sms" + (message_id != 0 ? "/" + message_id : ""), params, new JsonHttpResponseHandler() {
			            @Override
			            public void onSuccess(JSONObject res) {
			            	try {
								if(res.getBoolean("error"))
									OpenVBX.alert(context, "Message could not be sent.", res.getString("message"));
								else {
									OpenVBX.status(context, "Message sent");
									finish();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
			            }
			        });
				}
			}
		});
    }

    private boolean hasValidInput() {
    	from = spinner.getSelectedItem().toString();
    	to = ((EditText) findViewById(R.id.to)).getText().toString();
    	content = ((EditText) findViewById(R.id.content)).getText().toString();
    	return !"".equals(from) && !"".equals(to) && !"".equals(content);
    }

	@Override
	protected void onPause() {
		super.onPause();
		OpenVBX.dismissDialog();
	}
}