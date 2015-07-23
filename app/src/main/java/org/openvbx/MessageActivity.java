package org.openvbx;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.openvbx.adapters.AnnotationAdapter;
import org.openvbx.adapters.UserAdapter;
import org.openvbx.models.Message;
import org.openvbx.models.User;

import rx.functions.Action1;

public class MessageActivity extends AppCompatActivity {

    private int message_id;
    private Boolean isVoice = false;
	private MediaPlayer mediaPlayer = null;
	private SeekBar seekBar;
	private ImageView play;
	private Thread updateProgress = null;
	private String statuses[] = { "Open", "Closed", "Pending" };
	private Spinner status;
	private Spinner assigned;
	private UserAdapter userAdapter;
    private AnnotationAdapter annotationAdapter;
	private Message mMessage = new Message();
	private ListView annotationList;
	private LinearLayout progress;
    private Boolean isPaused = false;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progress = (LinearLayout) findViewById(R.id.progress);
        Bundle extras = getIntent().getExtras();
    	message_id = extras.getInt("message_id");
        isVoice = "voice".equals(mMessage.type);
    	annotationList = (ListView) findViewById(R.id.annotations);
        annotationAdapter = new AnnotationAdapter(this, R.layout.fragment_annotation_item, mMessage.annotations.items);
        annotationList.setAdapter(annotationAdapter);
    	status = (Spinner) findViewById(R.id.status);
    	ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statuses);
    	statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	status.setAdapter(statusAdapter);
    	status.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                final String status = statuses[pos].toLowerCase();
                OpenVBX.API.updateTicketStatus(message_id, status)
                        .compose(OpenVBX.<Void>defaultSchedulers())
                        .subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void ignored) {
                                // TODO - refresh view
                            }
                        });
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    	assigned = (Spinner) findViewById(R.id.assigned);
    	userAdapter = new UserAdapter(this, mMessage.active_users);
    	assigned.setAdapter(userAdapter);
    	assigned.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                final int user_id = mMessage.active_users.get(pos).id;
                OpenVBX.API.updateAssignment(message_id, user_id)
                        .compose(OpenVBX.<Void>defaultSchedulers())
                        .subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void ignored) {
                                // TODO - refresh view
                            }
                        });
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    	progress.setVisibility(View.VISIBLE);
        findViewById(R.id.reply).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), isVoice ? CallActivity.class : SmsActivity.class);
                i.putExtra("from", mMessage.original_called);
                i.putExtra("to", mMessage.original_caller);
                i.putExtra("message_id", message_id);
                startActivity(i);
            }
        });
    }

    public void refresh() {
        OpenVBX.API.getMessage(message_id)
                .compose(OpenVBX.<Message>defaultSchedulers())
                .subscribe(new Action1<Message>() {
                    @Override
                    public void call(Message message) {
                        mMessage = message;
                        isVoice = "voice".equals(message.type);
                        invalidateOptionsMenu();
                        ((TextView) findViewById(R.id.caller)).setText(message.caller);
                        ((TextView) findViewById(R.id.received_time)).setText(message.receivedTime());
                        TextView folder = (TextView) findViewById(R.id.folder);
                        folder.setText(message.folder);
                        if (message.folder.isEmpty())
                            folder.setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.summary)).setText(message.summary);
                        if (isVoice) {
                            for (int i = 0; i < statuses.length; i++)
                                if (statuses[i].toLowerCase().equals(message.ticket_status))
                                    status.setSelection(i);
                            status.setVisibility(View.VISIBLE);
                            if (!message.folder.isEmpty()) {
                                message.active_users.add(0, new User(0, "Select a", "user"));
                                userAdapter.update(message.active_users);
                                if (message.assigned != -1)
                                    assigned.setSelection(userAdapter.indexOf(message.assigned));
                                assigned.setVisibility(View.VISIBLE);
                            }
                            if (message.annotations.items.size() > 0) {
                                annotationAdapter.update(message.annotations.items);
                                annotationList.setVisibility(View.VISIBLE);
                            }
                            status.setVisibility(View.VISIBLE);
                            play = (ImageView) findViewById(R.id.play);
                            seekBar = (SeekBar) findViewById(R.id.seekbar);
                            seekBar.setOnTouchListener(new OnTouchListener() {
                                public boolean onTouch(View v, MotionEvent event) {
                                    if (mediaPlayer.isPlaying()) {
                                        SeekBar sb = (SeekBar) v;
                                        mediaPlayer.seekTo(sb.getProgress());
                                    }
                                    return false;
                                }
                            });
                            play.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(mediaPlayer.isPlaying()) {
                                        isPaused = true;
                                        mediaPlayer.pause();
                                        updateButtonState(false);
                                    }
                                    else {
                                        isPaused = false;
                                        int curr = seekBar.getProgress();
                                        int total = mediaPlayer.getDuration();
                                        seekBar.setMax(total);
                                        if(curr < total)
                                            mediaPlayer.seekTo(curr);
                                        else
                                            mediaPlayer.seekTo(0);
                                        mediaPlayer.start();
                                        updateButtonState(true);
                                        updateProgress = new Thread(new Runnable() {
                                            public void run() {
                                                Thread currentThread = Thread.currentThread();
                                                int curr = mediaPlayer.getCurrentPosition();
                                                int total = mediaPlayer.getDuration();
                                                seekBar.setProgress(curr);
                                                while (currentThread == updateProgress && mediaPlayer != null && mediaPlayer.isPlaying()) {
                                                    try {
                                                        curr = mediaPlayer.getCurrentPosition();
                                                        seekBar.setProgress(curr);
                                                        Thread.sleep(100);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                if(!isPaused) {
                                                    seekBar.setProgress(total);
                                                    updateButtonState(false);
                                                }
                                            }
                                        });
                                        updateProgress.start();
                                    }
                                }
                            });
                            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    if(fromUser)
                                        mediaPlayer.seekTo(progress);
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {
                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {
                                }
                            });
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            try {
                                mediaPlayer.setDataSource(message.recording_url);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    findViewById(R.id.audio).setVisibility(View.VISIBLE);
                                }
                            });
                            mediaPlayer.prepareAsync();
                        }
                        invalidateOptionsMenu();
                        progress.setVisibility(View.GONE);
                    }
                });
    }

    public void updateButtonState(final Boolean showPause) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(showPause) {
                    play.setImageResource(R.drawable.ic_pause_white_48dp);
                }
                else {
                    play.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_message, menu);
        menu.findItem(R.id.action_add_note).setVisible(isVoice);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent i;
        switch(id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_add_note:
                i = new Intent(getApplicationContext(), AnnotationActivity.class);
                i.putExtra("message_id", message_id);
                startActivity(i);
                break;
            case R.id.action_archive:
                // TODO - confirm before deleting
                OpenVBX.API.archiveMessage(message_id, true)
                        .compose(OpenVBX.<Void>defaultSchedulers())
                        .subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void ignored) {
                                OpenVBX.toast(R.string.message_archived);
                                finish();
                            }
                        });
                break;
        }
        return super.onOptionsItemSelected(item);
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