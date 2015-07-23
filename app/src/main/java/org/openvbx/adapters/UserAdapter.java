package org.openvbx.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.openvbx.models.User;

import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {

	public ArrayList<User> users;
	private LayoutInflater inflater;

	static class ViewHolder {
		TextView text;
	}

	public UserAdapter(Context context, ArrayList<User> users) {
		this.users = users;
		inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return users.size();
	}

	public User get(int position){
		return users.get(position);
	}

	public void add(User user){
		users.add(user);
	}

	public String getItem(int position){
		return get(position).getName();
	}
	
	public long getItemId(int position) {
		return users.indexOf(get(position));
	}

	public int indexOf(int id) {
		for(int i = 0; i < users.size(); i++)
			if(id == users.get(i).id)
				return i;
		return -1;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return createResource(position, convertView, parent, android.R.layout.simple_spinner_item);
	}

	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return createResource(position, convertView, parent, android.R.layout.simple_spinner_dropdown_item);
	}

	public View createResource(int position, View convertView, ViewGroup parent, int resource) {
		View v = convertView;
		ViewHolder vh;
		if (v == null) {
			v = inflater.inflate(resource, parent, false);
			vh = new ViewHolder();
			vh.text = (TextView) v.findViewById(android.R.id.text1);
			v.setTag(vh);
		} 
		else
			vh = (ViewHolder) v.getTag();
		vh.text.setText(get(position).getName());
		return v;
	}

	public void update(ArrayList<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }
}