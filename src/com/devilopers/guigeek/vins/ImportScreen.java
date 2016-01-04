package com.devilopers.guigeek.vins;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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

	  Log.e("Import", "OnCreate");
		setContentView(R.layout.import_screen);
		super.onCreate(savedInstanceState);

		Bundle bundle = this.getIntent().getExtras();
		if (bundle == null || bundle.get(TheWinesApp.WRAPPER) == null) {
			Toast.makeText(getApplicationContext(), "Bundle null", Toast.LENGTH_SHORT).show();
			finish();
		}
		else {
		  String aFilePath = (String) bundle.get(TheWinesApp.WRAPPER);
		  createWrapper(aFilePath);
		  
			if (wrapper == null || wrapper.getData() == null || wrapper.getData().size() == 0) {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_items_in_wrapper), Toast.LENGTH_SHORT).show();
				finish();
			}
			else {
			  Log.e("Import", "creating stuff");
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
					else if (vin.getColour().equals(DatabaseAdapter.COLOUR_FORTIFIED)) colour = getResources().getString(R.string.colour_fortified);
					else if (vin.getColour().equals(DatabaseAdapter.COLOUR_CHAMPAGNE)) colour = getResources().getString(R.string.colour_champ);
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



	private void createWrapper(String aFilePath) {
	  // Get the path of the Selected File.
	  wrapper = new WineVectorSerializer(false);
	  FileInputStream fis = null;
	  ObjectInputStream in = null;
	  
	  try {
	    fis = new FileInputStream(aFilePath);
	    in = new ObjectInputStream(fis);
	    
	    Object aSerializedObject = in.readObject();
	    if (aSerializedObject instanceof WineVectorSerializer) {
	      for (Vin v: ((WineVectorSerializer)aSerializedObject).getData()) {
	        wrapper.getData().add(v);
	      }
	    }
	    else if (aSerializedObject instanceof Vin) {
	      Vin aWine = (Vin) aSerializedObject;
	      while (aWine != null) {
	        wrapper.getData().add(aWine);
	        aWine =  (Vin) in.readObject();
	      }
	    }
	    else {
	      Toast.makeText(getApplicationContext(), getResources().getString(R.string.import_error), Toast.LENGTH_SHORT).show();
	      Log.e("Import", "Invalid file: unreckognized serialized class");
	      this.finish();
	    }
	  } 
	  catch (EOFException ex) {
	    Log.e("Import", "Reached EOF");
	  }
	  catch (Exception ex) {
	    Toast.makeText(getApplicationContext(), getResources().getString(R.string.import_error), Toast.LENGTH_SHORT).show();
	    Log.e("Import", "Exception caught when picking");
	    this.finish();
	  }
	  finally {
	    if (in != null) {
	      try {
	        in.close();
	      } catch (IOException e) {
	      }
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
							if (tryToInsert(wrapper.getData().get(i)) > -1) {
								System.out.println("Try to insert wrapper's item " + i);
								successfullyAdded++;
							}
							handle.sendMessage(handle.obtainMessage());
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

			if (totalItems > 0) {
			  mProgress = new ProgressDialog(ImportScreen.this);
	      mProgress.setMax(totalItems);
	      mProgress.setTitle(getResources().getString(R.string.import_title));
	      mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	      mProgress.setCancelable(false);

	      importThres.start();
	      mProgress.show();
			}
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
	  boolean imageCreated = false;
		if (vin.get_imageBytes() != null) {
			vin.setImagePath(null);
			imageCreated = createPicture(vin);
		}
		long result =  DatabaseAdapter.instance().addEntry(vin.getNom(), vin.getAppellation(), vin.getColour(), vin.getCepage(), 
				vin.getAccords(), vin.getDescription(), vin.getMillesime(), vin.getNote(), vin.getPrice(), vin.getPointOfSale(), vin.getAgingPotential(), vin.getStock(), vin.getImagePath(), 0, vin.isBuyAgain());
		
		// Add failed, let's delete the image
		if (result <= 0 && imageCreated) {
		  File aImage = new File(vin.getImagePath());
		  if (aImage.exists()) {
		    aImage.delete();
		    Log.e("impt", "deleted img as add failed");
		  }
		}
		
		return result;
	}



	private boolean createPicture(Vin vin) {
		if (vin.get_imageBytes() != null) {
			try {
				PictureManager aPicMgr = new PictureManager(ImportScreen.this);
				File aOutputFile = aPicMgr.createImageFile();
				FileOutputStream aFOS = new FileOutputStream(aOutputFile);
				
				Bitmap aBmp = BitmapFactory.decodeByteArray(vin.get_imageBytes(), 0, vin.get_imageBytes().length);
				aBmp.compress(Bitmap.CompressFormat.JPEG, 100, aFOS);
				aFOS.close();
				Log.e("Import", "Image saved to " + aOutputFile.getAbsolutePath());
				vin.setImagePath(aOutputFile.getAbsolutePath());
				return true;
			} 
			catch (FileNotFoundException e) {
				Log.e("Import", "File not found");
			}
			catch (NullPointerException ex) {
				Log.e("Import", "Image decoding failed");
			} catch (IOException e) {
				Log.e("Import", "IO ex");
			}
			finally {
				vin.freeImage();
			}
		}
		return false;
	}

}
