package org.openvbx;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import org.openvbx.adapters.CallerIDAdapter;
import org.openvbx.models.MessageResult;

import rx.functions.Action1;

public class CallActivity extends AppCompatActivity {

    private Activity mActivity;
    private Spinner spinner;
	private String callerid = null;
	private String to = null;
	private int message_id = -1;

    // TODO - switch call button to FAB
    // TODO - add custom dial pad
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;
        setContentView(R.layout.activity_call);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinner = (Spinner) findViewById(R.id.callerid);
        CallerIDAdapter adapter = new CallerIDAdapter(this, OpenVBX.getCallerIDs("voice"));
        spinner.setAdapter(adapter);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
        	spinner.setSelection(adapter.indexOf(extras.getString("from")));
        	((EditText) findViewById(R.id.to)).setText(extras.getString("to"));
        	message_id = extras.getInt("message_id", -1);
        }
		findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if(hasValidInput()) {
                    (message_id == -1 ? OpenVBX.API.sendCall(callerid, OpenVBX.device, to) : OpenVBX.API.sendCallback(message_id, callerid, OpenVBX.device, to))
                            .compose(OpenVBX.<MessageResult>defaultSchedulers())
                            .subscribe(new Action1<MessageResult>() {
                                @Override
                                public void call(MessageResult result) {
                                    if (result.error) {
                                        OpenVBX.error(mActivity, result.message);
                                    } else {
                                        OpenVBX.toast(R.string.calling);
                                        finish();
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
    	return !callerid.isEmpty() && !to.isEmpty();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}