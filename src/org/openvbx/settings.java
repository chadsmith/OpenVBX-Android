package org.openvbx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class settings extends Activity {

	private OpenVBXApplication OpenVBX;
	private String device = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        OpenVBX = (OpenVBXApplication) getApplication();
		((Button) findViewById(R.id.save)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (hasValidInput()) {
					OpenVBX.setDevice(device);
					Intent i = new Intent(getApplicationContext(), folders.class);
					startActivity(i);
				}
			}
		});
        ((EditText) findViewById(R.id.device)).setText(OpenVBX.getDevice());
    }

    private boolean hasValidInput() {
    	device = ((EditText) findViewById(R.id.device)).getText().toString();
    	return !"".equals(device);
    }
}