package com.devilopers.guigeek.vins;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import Vins.R;



public class SectionAdapter extends ArrayAdapter<Section> {
	
	private Section[] _items;
	
	public SectionAdapter(Context context, int textViewResourceId, Section[] items) {
		super(context, textViewResourceId, items);
		_items = items;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.list_section, null);
        }
        Section section = _items[position];
        if (section != null) {
	        TextView desc = (TextView) convertView.findViewById(R.id.listItemDescription);
	        if (desc != null) {
	        	desc.setText(section.getName());
	        }
        }
        return convertView;
	}

}
