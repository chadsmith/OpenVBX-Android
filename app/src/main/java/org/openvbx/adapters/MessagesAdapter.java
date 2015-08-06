package org.openvbx.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.openvbx.R;
import org.openvbx.models.Message;

import java.util.ArrayList;

public class MessagesAdapter extends ArrayAdapter<Message> {
	public ArrayList<Message> messages;
	private int ViewResourceId;
	private LayoutInflater inflater;

	public MessagesAdapter(Context context, int textViewResourceId, ArrayList<Message> messages) {
		super(context, textViewResourceId, messages);
		this.messages = messages;
		this.ViewResourceId = textViewResourceId;
		inflater = LayoutInflater.from(context);
	}

	public int indexOf(int id) {
		for(int i = 0; i < messages.size(); i++)
			if(id == messages.get(i).id)
				return i;
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(ViewResourceId, null);
		}
		Message message = messages.get(position);
		if (message != null) {
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
			TextView caller = (TextView) view.findViewById(R.id.caller);
			TextView folder = (TextView) view.findViewById(R.id.folder);
			TextView short_summary = (TextView) view.findViewById(R.id.short_summary);
			TextView received_time = (TextView) view.findViewById(R.id.received_time);
            icon.setImageResource("sms".equals(message.type) ? R.drawable.ic_message_white_48dp : R.drawable.ic_voicemail_white_48dp);
			caller.setText(message.caller);
			folder.setText(message.folder);
			if(message.folder.isEmpty())
				folder.setVisibility(View.GONE);
			short_summary.setText(message.short_summary);
			if(message.short_summary.isEmpty())
				short_summary.setVisibility(View.GONE);
			received_time.setText(message.receivedTime());
            int typeface = message.unread ? Typeface.BOLD : Typeface.NORMAL;
            caller.setTypeface(null, typeface);
            folder.setTypeface(null, typeface);
            short_summary.setTypeface(null, typeface);
            received_time.setTypeface(null, typeface);
		}
		return view;
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	public Message get(int position) {
		return messages.get(position);
	}

	public void update(ArrayList<Message> messages) {
		this.messages = messages;
		notifyDataSetChanged();
	}
}