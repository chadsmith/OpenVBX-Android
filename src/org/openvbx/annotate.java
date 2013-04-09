package org.openvbx;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class annotate extends Activity {

	private OpenVBXApplication OpenVBX;

	private Context context = this;
	
	private int message_id;
	private String description = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annotate);
        OpenVBX = (OpenVBXApplication) getApplication();
        Bundle extras = getIntent().getExtras();
        message_id = extras.getInt("id");
		((Button) findViewById(R.id.add)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (hasValidInput()) {
					OpenVBX.dialog(context, "Saving...");
					AsyncHttpClient client = new AsyncHttpClient();
					client.addHeader("Accept", "application/json");
					client.setBasicAuth(OpenVBX.getEmail(), OpenVBX.getPassword());
					RequestParams params = new RequestParams();
					params.put("annotation_type", "noted");
					params.put("description", description);
					client.post(OpenVBX.getServer() + "/messages/details/" + message_id + "/annotations", params, new JsonHttpResponseHandler() {
			            @Override
			            public void onSuccess(JSONObject res) {
							try {
								JSONObject annotation = res.getJSONObject("annotation");
								Intent i = new Intent();
								i.putExtra("id", annotation.getInt("id"));
								i.putExtra("first_name", annotation.getString("first_name"));
								i.putExtra("last_name", annotation.getString("last_name"));
								i.putExtra("description", annotation.getString("description"));
								OpenVBX.dismissDialog();
								setResult(RESULT_OK, i);
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
    	description = ((EditText) findViewById(R.id.description)).getText().toString();
    	return !"".equals(description);
    }

	@Override
	protected void onPause() {
		super.onPause();
		OpenVBX.dismissDialog();
	}
}