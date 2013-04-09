package org.openvbx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class inbox extends ListActivity {

	private OpenVBXApplication OpenVBX;
	private int folder_id;
	private ArrayList<Messages> list;
	private MessagesAdapter adapter;
	private LinearLayout progress;
	private PullToRefreshListView pullToRefreshListView;
	private ListView listView;
	private Context context = this;

	private static final int MENU_CALL = Menu.FIRST;
	private static final int MENU_TEXT = Menu.FIRST + 1;

	private static final int OPTION_ARCHIVE = Menu.FIRST + 2;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pullable_list);
        OpenVBX = (OpenVBXApplication) getApplication();
        progress = (LinearLayout) findViewById(R.id.progress);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pullable_listview);
        listView = pullToRefreshListView.getRefreshableView();
        Bundle extras = getIntent().getExtras();
    	folder_id = extras.getInt("id");
    	setTitle(extras.getString("name"));
    	list = new ArrayList<Messages>();
        adapter = new MessagesAdapter(this, R.layout.inbox, list);
        listView.setAdapter(adapter);
        pullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                refresh();
            }
        });
        progress.setVisibility(View.VISIBLE);
        refresh();
		registerForContextMenu(listView);
    }

    public void refresh() {
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Accept", "application/json");
		client.setBasicAuth(OpenVBX.getEmail(), OpenVBX.getPassword());
		client.get(OpenVBX.getServer() + "/messages/inbox/" + folder_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject res) {
                try {
					JSONArray messages = res.getJSONObject("messages").getJSONArray("items");
					list.clear();
					for(int i = 0; i < messages.length(); i++)
						list.add(new Messages(messages.getJSONObject(i).getInt("id"), messages.getJSONObject(i).getString("folder"), messages.getJSONObject(i).getString("type"), messages.getJSONObject(i).getString("caller"), messages.getJSONObject(i).getString("short_summary"), messages.getJSONObject(i).getBoolean("unread"), DateFormat.format("M/dd/yy", new SimpleDateFormat("yyyy-MMM-dd'T'HH:mm:ssZ").parse(messages.getJSONObject(i).getString("received_time"))).toString()));
					adapter.notifyDataSetChanged();
					progress.setVisibility(View.GONE);
					pullToRefreshListView.onRefreshComplete();
                } catch(JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
					e.printStackTrace();
				}
            }
        });
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	Messages message = adapter.get(position - 1);
    	Intent i = new Intent(this, message.class);
    	i.putExtra("id", message.getId());
        ((TextView) v.findViewById(R.id.caller)).setTypeface(null, Typeface.NORMAL);
        ((TextView) v.findViewById(R.id.folder)).setTypeface(null, Typeface.NORMAL);
        ((TextView) v.findViewById(R.id.short_summary)).setTypeface(null, Typeface.NORMAL);
        ((TextView) v.findViewById(R.id.received_time)).setTypeface(null, Typeface.NORMAL);
    	startActivityForResult(i, 0);
    	setResult(RESULT_OK);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 0 && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			int message_id = extras.getInt("message_id");
            for(int i = 0; i < list.size(); i++)
            	if(list.get(i).getId() == message_id)
            		list.remove(i);
    		adapter.notifyDataSetChanged();
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuItem new_call = menu.add(0, MENU_CALL, 0, "New Call");
    	MenuItem new_text = menu.add(0, MENU_TEXT, 0, "New Text");
    	new_call.setIcon(android.R.drawable.ic_menu_call);
    	new_text.setIcon(R.drawable.ic_menu_compose);
    	return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	Intent i;
    	switch(item.getItemId()) {
     		case MENU_CALL:
    			i = new Intent(getApplicationContext(), call.class);
    			startActivity(i);
    			return true;
    		case MENU_TEXT:
    			i = new Intent(getApplicationContext(), sms.class);
    			startActivity(i);
    			return true;
    	}
    	return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	menu.add(0, OPTION_ARCHIVE, 0, "Delete Message");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	final Messages message = adapter.get(info.position - 1);
    	switch(item.getItemId()) {
	      	case OPTION_ARCHIVE:
				AsyncHttpClient client = new AsyncHttpClient();
				client.addHeader("Accept", "application/json");
				client.setBasicAuth(OpenVBX.getEmail(), OpenVBX.getPassword());
				RequestParams params = new RequestParams();
				params.put("archived", "true");
				client.post(OpenVBX.getServer() + "/messages/details/" + message.getId(), params, new JsonHttpResponseHandler() {
		            @Override
		            public void onSuccess(JSONObject res) {
	                    list.remove(adapter.indexOf(message.getId()));
	                    adapter.notifyDataSetChanged();
	                    OpenVBX.status(context, "Message deleted");
	                    setResult(RESULT_OK);
		            }
		        });
	      		return true;
    	}
    	return super.onContextItemSelected(item);
    }
}