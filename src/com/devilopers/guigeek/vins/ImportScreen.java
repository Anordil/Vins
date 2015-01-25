package com.devilopers.guigeek.vins;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.guigeek.vins.R;

public class ImportScreen extends ListActivity  implements OnClickListener {
	Button validateButton;
	CheckBox checkAll;
	ListView theList;
	String[] wineLabels;
	WineVectorSerializer wrapper;
	
	private ProgressDialog mProgress;
	private int successfullyAdded = 0, totalItems = 0;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setContentView(R.layout.import_screen);
		super.onCreate(savedInstanceState);
		
		Bundle bundle = this.getIntent().getExtras();
		if (bundle == null) {
			Toast.makeText(getApplicationContext(), "Bundle null", Toast.LENGTH_SHORT).show();
			finish();
		}
		else {
			wrapper = (WineVectorSerializer)bundle.get(TheWinesApp.WRAPPER);
			if (wrapper == null || wrapper.getData() == null || wrapper.getData().size() == 0) {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_items_in_wrapper), Toast.LENGTH_SHORT).show();
				finish();
			}
			else {
				validateButton = (Button) findViewById(R.id.validateImportButton);
				validateButton.setOnClickListener(this);
				checkAll = (CheckBox) findViewById(R.id.checkAll);
				checkAll.setOnClickListener(this);
				
				wineLabels = new String[wrapper.getData().size()];
				int i = 0;
				String colour;
				for (Vin vin : wrapper.getData()) {
					if (vin.getColour().equals(DatabaseAdapter.COLOUR_RED)) colour = getResources().getString(R.string.colour_red);
					else if (vin.getColour().equals(DatabaseAdapter.COLOUR_ROSE)) colour = getResources().getString(R.string.colour_rose);
					else if (vin.getColour().equals(DatabaseAdapter.COLOUR_WHITE)) colour = getResources().getString(R.string.colour_white);
					else colour = getResources().getString(R.string.colour_yellow);
					wineLabels[i] = vin.getNom() + " " + vin.getMillesime() + " (" + vin.getAppellation() + ") - " + colour + " " + vin.getNote() + "/10";
					++i;
				}
				//Toast.makeText(getApplicationContext(), "Nbr d'objets: " + wrapper.getList().size(), Toast.LENGTH_SHORT).show();
				setListAdapter(new ArrayAdapter<String>(this, R.layout.import_list, wineLabels));
				
				theList = getListView();
				theList.setItemsCanFocus(false);
				theList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			}
		}
	}


	
	@Override
	public void onClick(View v) {
		// The import button is clicked -> try to import the selected wines
		if (v == validateButton) {
		  
      final Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
          super.handleMessage(msg);
          doProgress();
        }
      };
		  
		  Thread importThres = new Thread(new Runnable() {
        
        public void run() {
          
          for (int i = 0; i < theList.getCount(); ++i) {
            if (theList.isItemChecked(i)) {
              // Error cases are -1 and -2 so anything higher means the INSERT was successful
              handle.sendMessage(handle.obtainMessage());
              if (tryToInsert(wrapper.getData().get(i)) > -1) {
                System.out.println("Try to insert wrapper's item " + i);
                successfullyAdded++;
              }
            }
          }
        }
      });
		  
		  totalItems = 0;
		  successfullyAdded = 0;
	    for (int i = 0; i < theList.getCount(); ++i) {
	      if (theList.isItemChecked(i)) {
	        totalItems++;
	      }
	    }
	    
	    mProgress = new ProgressDialog(ImportScreen.this);
	    mProgress.setMax(totalItems);
		  mProgress.setTitle(getResources().getString(R.string.import_title));
		  mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		  
		  importThres.start();
		  mProgress.show();
		}
		
		else if (v == checkAll) {
			for (int i = 0; i < theList.getCount(); ++i) {
				theList.setItemChecked(i, checkAll.isChecked());
			}
		}
	}
	
	private void doProgress() {
	  mProgress.incrementProgressBy(1);
	  mProgress.setMessage(mProgress.getProgress() + "/" + mProgress.getMax());
	  
	  if (mProgress.getProgress() == mProgress.getMax()) {
	    mProgress.dismiss();
      Toast.makeText(getApplicationContext(), 
          successfullyAdded + " " + getResources().getString(R.string.out_of) + " " + totalItems + " " + getResources().getString(R.string.were_added), 
          Toast.LENGTH_SHORT).show();
	  }
	}
	
	

	private long tryToInsert(Vin vin) {
		// Try to add this wine to the DB : it may fail
		return DatabaseAdapter.instance().addEntry(vin.getNom(), vin.getAppellation(), vin.getColour(), vin.getCepage(), 
				vin.getAccords(), vin.getDescription(), vin.getMillesime(), vin.getNote(), vin.getPrice(), vin.getPointOfSale(), vin.getAgingPotential(), vin.getStock(), vin.getImagePath(), 0);
	}

}
