package org.openvbx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.openvbx.models.Message;

import rx.functions.Action1;

// TODO - move to dialog
public class AnnotationActivity extends AppCompatActivity {

    private Message message;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_annotation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        message = extras.getParcelable("message");
		findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
                String description = ((EditText) findViewById(R.id.description)).getText().toString();
                if (!description.isEmpty()) {
					OpenVBX.toast(R.string.saving);
                    OpenVBX.API.addAnnotation(message.id, "noted", description)
                            .compose(OpenVBX.<Void>defaultSchedulers())
                            .subscribe(new Action1<Void>() {
                                @Override
                                public void call(Void ignored) {
                                    finish();
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