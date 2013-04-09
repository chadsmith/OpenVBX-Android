package org.openvbx;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CallerIDAdapter extends BaseAdapter {
	public ArrayList<CallerID> callerids;
	private LayoutInflater inflater;

	static class ViewHolder {
		TextView text;
	}

	public CallerIDAdapter(Context context, ArrayList<CallerID> callerids) {
		this.callerids = callerids;
		inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return callerids.size();
	}

	public CallerID get(int position){
		return callerids.get(position);
	}

	public void clear(){
		callerids.clear();
	}

	public void add(CallerID callerid){
		callerids.add(callerid);
	}

	public String getItem(int position){
		return get(position).getName();
	}
	
	public long getItemId(int position) {
		return callerids.indexOf(get(position));
	}

	public int indexOf(String number) {
		for(int i = 0; i < callerids.size(); i++)
			if(number.equals(callerids.get(i).getNumber()))
				return i;
		return 0;
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
}