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

public class call extends Activity {

	private OpenVBXApplication OpenVBX;

	private CallerIDAdapter adapter;
	private Spinner spinner;
	private Context context = this;

	private String callerid = null;
	private String to = null;
	private int message_id = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);
        OpenVBX = (OpenVBXApplication) getApplication();
        spinner = (Spinner) findViewById(R.id.callerid);
        adapter = new CallerIDAdapter(this, OpenVBX.getCallerIDs("voice"));
        spinner.setAdapter(adapter);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
        	spinner.setSelection(adapter.indexOf(extras.getString("from")));
        	((EditText) findViewById(R.id.to)).setText(extras.getString("to"));
        	message_id = extras.getInt("message_id");
        }
		((Button) findViewById(R.id.call)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if(hasValidInput()) {
					OpenVBX.status(context, "Calling...");
					AsyncHttpClient client = new AsyncHttpClient();
					client.addHeader("Accept", "application/json");
					client.setBasicAuth(OpenVBX.getEmail(), OpenVBX.getPassword());
					RequestParams params = new RequestParams();
					params.put("callerid", callerid);
					params.put("from", OpenVBX.getDevice());
					params.put("to", to);
					client.post(OpenVBX.getServer() + "/messages/call" + (message_id != 0 ? "/" + message_id : ""), params, new JsonHttpResponseHandler() {
			            @Override
			            public void onSuccess(JSONObject res) {
			            	try {
								if(res.getBoolean("error"))
									OpenVBX.alert(context, "Call could not be started.", res.getString("message"));
								else
									finish();
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
    	callerid = spinner.getSelectedItem().toString();
    	to = ((EditText) findViewById(R.id.to)).getText().toString();
    	return !"".equals(callerid) && !"".equals(to);
    }

	@Override
	protected void onPause() {
		super.onPause();
		OpenVBX.dismissDialog();
	}
}