package org.openvbx.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.openvbx.R;
import org.openvbx.models.Annotation;

import java.util.ArrayList;

public class AnnotationAdapter extends ArrayAdapter<Annotation> {
	public ArrayList<Annotation> annotations;
	private int ViewResourceId;
	private LayoutInflater inflater;

	public AnnotationAdapter(Context context, int textViewResourceId, ArrayList<Annotation> annotations) {
		super(context, textViewResourceId, annotations);
		this.annotations = annotations;
		this.ViewResourceId = textViewResourceId;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(ViewResourceId, null);
		}
		Annotation annotation = annotations.get(position);
		if (annotation != null) {
			TextView name = (TextView) view.findViewById(R.id.name);
			TextView description = (TextView) view.findViewById(R.id.description);
			name.setText(annotation.getName());
			description.setText(annotation.description);
		}
		return view;
	}

	@Override
	public int getCount() {
		return annotations.size();
	}

	public Annotation get(int position) {
		return annotations.get(position);
	}

	public void update(ArrayList<Annotation> annotations) {
		this.annotations = annotations;
		notifyDataSetChanged();
	}
}