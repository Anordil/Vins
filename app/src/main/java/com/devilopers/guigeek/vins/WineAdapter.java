package com.devilopers.guigeek.vins;
import java.util.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import Vins.R;



public class WineAdapter extends ArrayAdapter<Vin> {
	
	private Vin[] _items;
	
	public WineAdapter(Context context, int textViewResourceId, Vin[] items) {
		super(context, textViewResourceId, items);
		_items = items;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.list_wine, null);
        }
        Vin v = _items[position];
        if (v != null) {
	        TextView desc = (TextView) convertView.findViewById(R.id.listItemDescription);
	        TextView note = (TextView) convertView.findViewById(R.id.listItemNote);
	        TextView stock = (TextView) convertView.findViewById(R.id.listItemStock);
	        TextView location = (TextView) convertView.findViewById(R.id.listItemLocation);
	        TextView aging = (TextView) convertView.findViewById(R.id.listItemAgingLeft);
	        ImageView icon = (ImageView) convertView.findViewById(R.id.listItemImage);
	        ImageView caddie = (ImageView) convertView.findViewById(R.id.listItemImageBuy);
	        ImageView hourglass = (ImageView) convertView.findViewById(R.id.listItemTimer);
	        
	        // Don't display empty vintages
	        String vintage = v.getMillesime() == 0 ? "" : "" + v.getMillesime();
	        
	        if (desc != null) {
	        	desc.setText(v.getNom() + " " + vintage + " (" + v.getAppellation() + ")" 
//	        			+ v.getStock() + " " + TheWinesApp.getContext().getResources().getString(R.string.bottles)
	        			);
	        }
	        if (stock != null) {
	        	stock.setText("" + v.getStock());
	        }
	        if (location != null && v.getLocation() > 0) {
	        	location.setVisibility(View.VISIBLE);
	        	location.setText(DatabaseAdapter.instance().getCompartmentLongName(v.getLocation()));
	        }
	        else {
	        	location.setVisibility(View.GONE);
	        }
	        if(note != null){
	        	note.setText("" + v.getNote() + "★");
	        }
	        if (icon != null) {
	        	if (v.getColour().equals(DatabaseAdapter.COLOUR_RED)) {
	        		icon.setImageDrawable(this.getContext().getResources().getDrawable(R.drawable.glass_rouge));
	        	}
	        	if (v.getColour().equals(DatabaseAdapter.COLOUR_ROSE)) {
	        		icon.setImageDrawable(this.getContext().getResources().getDrawable(R.drawable.glass_rose));
	        	}
	        	if (v.getColour().equals(DatabaseAdapter.COLOUR_WHITE)) {
	        		icon.setImageDrawable(this.getContext().getResources().getDrawable(R.drawable.glass_blanc));
	        	}
	        	if (v.getColour().equals(DatabaseAdapter.COLOUR_YELLOW)) {
	        		icon.setImageDrawable(this.getContext().getResources().getDrawable(R.drawable.glass_jaune));
	        	}
	        	if (v.getColour().equals(DatabaseAdapter.COLOUR_CHAMPAGNE)) {
	        		icon.setImageDrawable(this.getContext().getResources().getDrawable(R.drawable.glass_champagne));
	        	}
	        	if (v.getColour().equals(DatabaseAdapter.COLOUR_FORTIFIED)) {
	        		icon.setImageDrawable(this.getContext().getResources().getDrawable(R.drawable.glass_forti));
	        	}
	        }
	        if (caddie != null && !v.isBuyAgain()) {
	        	caddie.setVisibility(View.GONE);
	        }
	        else if (caddie != null) {
	        	caddie.setVisibility(View.VISIBLE);
	        }
	        
	        // Aging left
	        String agingLeft = "?";
	        aging.setTextColor(0xFF000000);
	        hourglass.setImageDrawable(this.getContext().getResources().getDrawable(R.drawable.timer));
	        if (v.getMillesime() > 0 && v.getAgingPotential() > 0) {
	          int targetYear = v.getMillesime() + v.getAgingPotential();
	          int currentYear = Calendar.getInstance().get(Calendar.YEAR);
	          
	          int agingLeftInt = targetYear - currentYear;
	          if (agingLeftInt <= -5) {
              agingLeft = "✔";
              hourglass.setImageDrawable(this.getContext().getResources().getDrawable(R.drawable.timer_empty));
              aging.setTextColor(0xFFFFA800);
            }
	          else if (agingLeftInt <= 0) {
	            agingLeft = "✔";
	            hourglass.setImageDrawable(this.getContext().getResources().getDrawable(R.drawable.timer_empty));
	            aging.setTextColor(0xFF20aa00);
	          }
	          else {
	            agingLeft = String.valueOf(agingLeftInt);
	          }
	          
	          if (agingLeftInt == 1) {
	            aging.setTextColor(0xFFFFA800);
	          }
	        }
	        aging.setText(agingLeft);
        }
        return convertView;
	}

}
