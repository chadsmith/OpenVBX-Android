package org.openvbx;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessagesAdapter extends ArrayAdapter<Messages> {
	public ArrayList<Messages> messages;
	private int ViewResourceId;
	private LayoutInflater inflater;

	public MessagesAdapter(Context context, int textViewResourceId, ArrayList<Messages> messages) {
		super(context, textViewResourceId, messages);
		this.messages = messages;
		this.ViewResourceId = textViewResourceId;
		inflater = LayoutInflater.from(context);
	}

	public int indexOf(int id) {
		for(int i = 0; i < messages.size(); i++)
			if(id == messages.get(i).getId())
				return i;
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(ViewResourceId, null);
		}
		Messages message = messages.get(position);
		if (message != null) {
			if("sms".equals(message.getType()))
				((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.sms);
			TextView caller = (TextView) view.findViewById(R.id.caller);
			TextView folder = (TextView) view.findViewById(R.id.folder);
			TextView short_summary = (TextView) view.findViewById(R.id.short_summary);
			TextView received_time = (TextView) view.findViewById(R.id.received_time);
			caller.setText(message.getCaller());
			folder.setText(message.getFolder());
			if("".equals(message.getFolder()))
				folder.setVisibility(View.GONE);
			short_summary.setText(message.getSummary());
			if("".equals(message.getSummary()))
				short_summary.setVisibility(View.GONE);
			received_time.setText(message.getTime());
			if(message.isUnread()) {
				caller.setTypeface(null, Typeface.BOLD);
				folder.setTypeface(null, Typeface.BOLD);
				short_summary.setTypeface(null, Typeface.BOLD);
				received_time.setTypeface(null, Typeface.BOLD);
			}
		}
		return view;
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	public Messages get(int position) {
		return messages.get(position);
	}
}