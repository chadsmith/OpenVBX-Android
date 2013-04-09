package org.openvbx;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class message extends Activity {

	private OpenVBXApplication OpenVBX;
	private int message_id;
	private MediaPlayer mediaPlayer = null;
	private SeekBar seekBar;
	private ImageView play;
	private Thread updateProgress = null;
	private String type = null;
	private String original_caller = null;
	private String original_called = null;
	private String statuses[] = { "Open", "Closed", "Pending" };
	private Spinner status;
	private Spinner assigned;
	private UserAdapter userAdapter;
	private Button add;
	private AnnotationAdapter annotationAdapter;
	private ArrayList<User> users;
	private ArrayList<Annotation> annotations;
	private ListView annotationList;
	private LinearLayout progress;

	private Context context = this;

	private static final int MENU_REPLY = Menu.FIRST;
	private static final int MENU_ARCHIVE = Menu.FIRST + 1;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
        OpenVBX = (OpenVBXApplication) getApplication();
        progress = (LinearLayout) findViewById(R.id.progress);
        Bundle extras = getIntent().getExtras();
    	message_id = extras.getInt("id");
    	users = new ArrayList<User>();
    	add = (Button) findViewById(R.id.add);
    	annotationList = (ListView) findViewById(R.id.annotations);
        annotations = new ArrayList<Annotation>();
        annotationAdapter = new AnnotationAdapter(this, R.layout.annotation, annotations);
        annotationList.setAdapter(annotationAdapter);
    	status = (Spinner) findViewById(R.id.status);
    	ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statuses);
    	statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	status.setAdapter(statusAdapter);
    	status.setOnItemSelectedListener(new OnItemSelectedListener() {
    	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    			AsyncHttpClient client = new AsyncHttpClient();
    			client.addHeader("Accept", "application/json");
    			client.setBasicAuth(OpenVBX.getEmail(), OpenVBX.getPassword());
    			RequestParams params = new RequestParams();
    			params.put("ticket_status", statuses[pos].toLowerCase());
    			client.post(OpenVBX.getServer() + "/messages/details/" + message_id, params, new JsonHttpResponseHandler() {
    	            @Override
    	            public void onSuccess(JSONObject res) {
    	            }
    	        });
    	    }
    	    public void onNothingSelected(AdapterView<?> parent) {}
    	});
    	assigned = (Spinner) findViewById(R.id.assigned);
    	userAdapter = new UserAdapter(this, users);
    	assigned.setAdapter(userAdapter);
    	assigned.setOnItemSelectedListener(new OnItemSelectedListener() {
    	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    			AsyncHttpClient client = new AsyncHttpClient();
    			client.addHeader("Accept", "application/json");
    			client.setBasicAuth(OpenVBX.getEmail(), OpenVBX.getPassword());
    			RequestParams params = new RequestParams();
    			params.put("assigned", "" + users.get(pos).getId());
    			client.post(OpenVBX.getServer() + "/messages/details/" + message_id, params, new JsonHttpResponseHandler() {
    	            @Override
    	            public void onSuccess(JSONObject res) {
    	            }
    	        });
    	    }
    	    public void onNothingSelected(AdapterView<?> parent) {}
    	});
    	progress.setVisibility(View.VISIBLE);
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Accept", "application/json");
		client.setBasicAuth(OpenVBX.getEmail(), OpenVBX.getPassword());
		client.get(OpenVBX.getServer() + "/messages/details/" + message_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject res) {
                try {
                	original_called = res.getString("original_called");
                    original_caller = res.getString("original_caller");
                    ((TextView) findViewById(R.id.caller)).setText(res.getString("caller"));
                    ((TextView) findViewById(R.id.received_time)).setText(DateFormat.format("M/d/yy h:mm AA", new SimpleDateFormat("yyyy-MMM-dd'T'HH:mm:ssZ").parse(res.getString("received_time"))).toString());
                    TextView folder = (TextView) findViewById(R.id.folder);
                    folder.setText(res.getString("folder"));
                    if("".equals(res.getString("folder")))
                    	folder.setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.summary)).setText(res.getString("summary"));
                    type = res.getString("type");
                    if("voice".equals(type)) {
                        for(int i = 0; i < statuses.length; i++)
                    		if(statuses[i].toLowerCase().equals(res.getString("ticket_status")))
                    			status.setSelection(i);
                        status.setVisibility(View.VISIBLE);
                        if(!"".equals(res.getString("folder"))) {
	                        JSONArray active_users = res.getJSONArray("active_users");
	                        users.add(new User(0, "Select a", "user"));
	                        for(int i = 0; i < active_users.length(); i++)
	                        	users.add(new User(active_users.getJSONObject(i).getInt("id"), active_users.getJSONObject(i).getString("first_name"), active_users.getJSONObject(i).getString("last_name")));
	                        userAdapter.notifyDataSetChanged();
	                        if(!"null".equals(res.getString("assigned")))
	                        	assigned.setSelection(userAdapter.indexOf(res.getInt("assigned")));
	                        assigned.setVisibility(View.VISIBLE);
                        }
                        add.setVisibility(View.VISIBLE);
                        JSONArray annotation_items = res.getJSONObject("annotations").getJSONArray("items");
                        for(int i = 0; i < annotation_items.length(); i++)
                        	annotations.add(new Annotation(annotation_items.getJSONObject(i).getInt("id"), annotation_items.getJSONObject(i).getString("first_name"), annotation_items.getJSONObject(i).getString("last_name"), annotation_items.getJSONObject(i).getString("description")));
                        if(annotations.size() > 0) {
                        	annotationAdapter.notifyDataSetChanged();
                        	annotationList.setVisibility(View.VISIBLE);
                        }
                        status.setVisibility(View.VISIBLE);
                    	play = (ImageView) findViewById(R.id.play);
                    	seekBar = (SeekBar) findViewById(R.id.seekbar);
                    	seekBar.setOnTouchListener(new OnTouchListener() {
                    		public boolean onTouch(View v, MotionEvent event) {
                    	        if(mediaPlayer.isPlaying()) {
                    	            SeekBar sb = (SeekBar) v;
                    	            mediaPlayer.seekTo(sb.getProgress());
                    	        }
	                            return false;
                            }
                        });
                    	play.setOnTouchListener(new OnTouchListener() {
                    		public boolean onTouch(View v, MotionEvent event) {
	                            mediaPlayer.start();
								updateProgress = new Thread(new Runnable() {
									public void run() {
										Thread currentThread = Thread.currentThread();
										int curr = mediaPlayer.getCurrentPosition();
										int total = mediaPlayer.getDuration();
			                	    	seekBar.setProgress(curr);
			                	    	seekBar.setMax(total);
										while(currentThread == updateProgress && mediaPlayer != null && mediaPlayer.isPlaying()) {
											try {
												curr = mediaPlayer.getCurrentPosition();
												seekBar.setProgress(curr);
												Thread.sleep(100);
											}
											catch (InterruptedException e) {
												e.printStackTrace();
											}
										}
										seekBar.setProgress(total);
									}
								});
								updateProgress.start();
								return false;
                            }
                        });
                    	mediaPlayer = new MediaPlayer();
	                	mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	                	// TODO - Import Twilio SSL certificate for older versions of Android
	                	mediaPlayer.setDataSource(res.getString("recording_url").replace("https", "http"));
	                	mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
	                	    @Override
	                	    public void onPrepared(MediaPlayer mp) {
	                	    	((RelativeLayout) findViewById(R.id.audio)).setVisibility(View.VISIBLE);
	                	    }
	                	});
	                	mediaPlayer.prepareAsync();
                    }
                    progress.setVisibility(View.GONE);
                } catch(JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        });
		add.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
	        	Intent i = new Intent(getApplicationContext(), annotate.class);
	        	i.putExtra("id", message_id);
	        	startActivityForResult(i, 0);
			}
		});
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 0 && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
        	annotations.add(0, new Annotation(extras.getInt("id"), extras.getString("first_name"), extras.getString("last_name"), extras.getString("description")));
        	annotationAdapter.notifyDataSetChanged();
        	annotationList.setVisibility(View.VISIBLE);
		}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuItem return_call = menu.add(0, MENU_REPLY, 0, "Reply");
    	MenuItem archive_message = menu.add(0, MENU_ARCHIVE, 0, "Delete");
    	return_call.setIcon("voice".equals(type) ? android.R.drawable.ic_menu_call : R.drawable.ic_menu_compose);
    	archive_message.setIcon(android.R.drawable.ic_menu_delete);
    	return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch(item.getItemId()) {
	 		case MENU_REPLY:
	 			Intent i = new Intent(getApplicationContext(), "voice".equals(type) ? call.class : sms.class);
		    	i.putExtra("from", original_called);
		    	i.putExtra("to", original_caller);
		    	i.putExtra("message_id", message_id);
				startActivity(i);
				return true;
    		case MENU_ARCHIVE:
    			OpenVBX.status(context, "Deleting...");
    			AsyncHttpClient client = new AsyncHttpClient();
    			client.addHeader("Accept", "application/json");
    			client.setBasicAuth(OpenVBX.getEmail(), OpenVBX.getPassword());
    			RequestParams params = new RequestParams();
    			params.put("archived", "true");
    			client.post(OpenVBX.getServer() + "/messages/details/" + message_id, params, new JsonHttpResponseHandler() {
    	            @Override
    	            public void onSuccess(JSONObject res) {
    	            	Intent i = new Intent();
    	            	i.putExtra("message_id", message_id);
    	            	setResult(RESULT_OK, i);
                        finish();
    	            }
    	        });
    			return true;
    	}
    	return super.onMenuItemSelected(featureId, item);
    }

	@Override
	protected void onPause() {
		super.onPause();
		updateProgress = null;
		if(mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}
}