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

public class SmsActivity extends AppCompatActivity {

    private Activity mActivity;
    private Spinner spinner;
    private EditText toInput;
	private int message_id = -1;

    // TODO - switch send button to FAB
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;
        setContentView(R.layout.activity_sms);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinner = (Spinner) findViewById(R.id.from);
        CallerIDAdapter adapter = new CallerIDAdapter(this, OpenVBX.getCallerIDs("sms"));
        spinner.setAdapter(adapter);

        toInput = (EditText) findViewById(R.id.to);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
        	spinner.setSelection(adapter.indexOf(extras.getString("from")));
            toInput.setText(extras.getString("to"));
        	message_id = extras.getInt("message_id", -1);
        }

		findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String from = spinner.getSelectedItem().toString();
                String to = toInput.getText().toString().trim();
                String content = ((EditText) findViewById(R.id.content)).getText().toString();
                if (!from.isEmpty() && !to.isEmpty() && !content.isEmpty()) {
                    (message_id == -1 ? OpenVBX.API.sendMessage(from, to, content) : OpenVBX.API.sendReply(message_id, from, to, content))
                            .compose(OpenVBX.<MessageResult>defaultSchedulers())
                            .subscribe(new Action1<MessageResult>() {
                                @Override
                                public void call(MessageResult result) {
                                    if (result.error)
                                        OpenVBX.error(mActivity, result.message);
                                    else {
                                        OpenVBX.toast(R.string.message_sent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
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