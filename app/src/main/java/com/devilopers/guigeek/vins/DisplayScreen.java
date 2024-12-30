package com.devilopers.guigeek.vins;

import java.io.File;
import java.io.Serializable;
import java.util.Currency;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import Vins.R;

public class DisplayScreen extends Activity {
	
	private Vin vin;
	private RatingBar _displayedBar;
	private File imageFile = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.displaywine);
        
        ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
        
        Bundle bundle = this.getIntent().getExtras();
        Serializable data = bundle.getSerializable(Constants.DATA_WINE);
        if (data == null || !(data instanceof Vin)) {
          Toast.makeText(getApplicationContext(), getResources().getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
          this.finish();
        }
        vin = (Vin)data;
        
        //Fill in the info fields from the DB object
        initFields();
        
        processPreferences();
    }
	
	private void initFields() {
		TextView 
			viewNom 			= ((TextView)findViewById(R.id.inputNom)),
			viewAppellation 	= ((TextView)findViewById(R.id.inputAppellation)),
			viewNomEtAnnee		= ((TextView)findViewById(R.id.inputNomEtMillesime)),
			viewMillesime 		= ((TextView)findViewById(R.id.inputMillesime)),
			viewCepage 			= ((TextView)findViewById(R.id.inputCepage)),
			viewAccords 		= ((TextView)findViewById(R.id.inputAccords)),
			viewDescription 	= ((TextView)findViewById(R.id.inputDescription)),
			viewPrix 			= ((TextView)findViewById(R.id.inputPrix)),
			viewLieu 			= ((TextView)findViewById(R.id.inputPOS)),
			viewAging 			= ((TextView)findViewById(R.id.inputGarde)),
			viewColour			= ((TextView)findViewById(R.id.inputColour)),
			viewCurrency		= ((TextView)findViewById(R.id.currency)),
			viewStock			= ((TextView)findViewById(R.id.inputStock)),
		  viewLocation = (TextView)findViewById(R.id.inputLocation);
		RatingBar 
			viewNoteRed  			= ((RatingBar)findViewById(R.id.input_note_red)),
			viewNoteRose  			= ((RatingBar)findViewById(R.id.input_note_rose)),
			viewNoteWhite  			= ((RatingBar)findViewById(R.id.input_note_white)),
			viewNoteChamp  			= ((RatingBar)findViewById(R.id.input_note_champ)),
			viewNoteforti  			= ((RatingBar)findViewById(R.id.input_note_forti)),
			viewNoteYellow  		= ((RatingBar)findViewById(R.id.input_note_yellow));
		
		LinearLayout 
			panelCepage 		= (LinearLayout)findViewById(R.id.panelCepage),
			panelGarde			= (LinearLayout)findViewById(R.id.panelGarde),
			panelAccords		= (LinearLayout)findViewById(R.id.panelAccords),
			panelDescription	= (LinearLayout)findViewById(R.id.panelDescription),
			panelPOS			= (LinearLayout)findViewById(R.id.panelPOS),
			panelPrix			= (LinearLayout)findViewById(R.id.panelPrix);
		
		viewNom.setText(vin.getNom());
		viewAppellation.setText(vin.getAppellation());
		viewMillesime.setText(String.valueOf(vin.getMillesime()));
		
		viewCepage.setText(vin.getCepage());
		viewAccords.setText(vin.getAccords());
		viewDescription.setText(vin.getDescription());
 		viewPrix.setText(String.valueOf(vin.getPrice()));
		viewLieu.setText(vin.getPointOfSale());
		viewAging.setText(String.valueOf(vin.getAgingPotential()));
		viewColour.setText(vin.getColour());
		viewStock.setText(
				vin.getStock() + " " + getResources().getString(R.string.stockSuffix) 
				+ ( vin.isBuyAgain() ? " " + getResources().getString(R.string.buyAgainStock) : ""));
		
		if (viewLocation != null) {
		  viewLocation.setText(DatabaseAdapter.instance().getCompartmentLongName(vin.getLocation()));
		}
		
		
		viewCurrency.setText(Currency.getInstance(Locale.getDefault()).getSymbol());
		// Otherwise, we cannot display the year just after the name if the later is more than one line long
		
		// Don't display empty vintage
		if (Integer.parseInt(viewMillesime.getText().toString()) == 0) {
			viewNomEtAnnee.setText(viewNom.getText().toString() + " - " + getResources().getString(R.string.no_vintage));
		}
		else {
			viewNomEtAnnee.setText(viewNom.getText().toString() + " - " + viewMillesime.getText().toString());
		}
		
		
		
		//Show just one rating bar
		viewNoteRed.setVisibility(View.GONE);
		viewNoteRose.setVisibility(View.GONE);
		viewNoteWhite.setVisibility(View.GONE);
		viewNoteYellow.setVisibility(View.GONE);
		viewNoteforti.setVisibility(View.GONE);
		viewNoteChamp.setVisibility(View.GONE);
		
		//Hide the colour
		viewColour.setVisibility(View.GONE);
		
		String colour = viewColour.getText().toString();
		if (colour.equals(DatabaseAdapter.COLOUR_RED)) {
			viewNoteRed.setVisibility(View.VISIBLE);
			_displayedBar = viewNoteRed;
		}
		if (colour.equals(DatabaseAdapter.COLOUR_WHITE)){
			viewNoteWhite.setVisibility(View.VISIBLE);
			_displayedBar = viewNoteWhite;
		}
		if (colour.equals(DatabaseAdapter.COLOUR_ROSE)) {
			viewNoteRose.setVisibility(View.VISIBLE);
			_displayedBar = viewNoteRose;
		}
		if (colour.equals(DatabaseAdapter.COLOUR_YELLOW)) {
			viewNoteYellow.setVisibility(View.VISIBLE);
			_displayedBar = viewNoteYellow;
		}
		if (colour.equals(DatabaseAdapter.COLOUR_CHAMPAGNE)) {
			viewNoteChamp.setVisibility(View.VISIBLE);
			_displayedBar = viewNoteChamp;
		}
		if (colour.equals(DatabaseAdapter.COLOUR_FORTIFIED)) {
			viewNoteforti.setVisibility(View.VISIBLE);
			_displayedBar = viewNoteforti;
		}
		_displayedBar.setRating(vin.getNote());
		

		
		//Hide unused panels
		panelCepage.setVisibility( viewCepage.getText().length() == 0 ? View.GONE : View.VISIBLE);
		panelGarde.setVisibility( viewAging.getText().length() == 0 ? View.GONE : View.VISIBLE);
		panelAccords.setVisibility( viewAccords.getText().length() == 0 ? View.GONE : View.VISIBLE);
		panelDescription.setVisibility( viewDescription.getText().length() == 0 ? View.GONE : View.VISIBLE);
		panelPOS.setVisibility( viewLieu.getText().length() == 0 ? View.GONE : View.VISIBLE);
		panelPrix.setVisibility( viewPrix.getText().length() == 0 ? View.GONE : View.VISIBLE);
		
    // Image
		SharedPreferences prefs = getSharedPreferences(Constants.PREF, MODE_PRIVATE);
		if(prefs.getBoolean(Constants.PREF_PICTURE, true)) {
		  String imagePath = vin.getImagePath();
		  if (imagePath != null && imagePath.length() > 0) {
		    imageFile = new File(imagePath);
		    if (imageFile.exists()) {
		      displayNewPicture(imageFile);
		    }
		  } 
		}
	}
	
	// Exact same method as in AddScreen, clean this up asap :)
  // Gets the new image's size, decodes a scaled down version and displays it in _viewPicture
  protected void displayNewPicture(File pictureFile) {
    if (pictureFile == null) {
      return;
    }
    
    // Screen width
    Display d = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    int screenWidth = d.getWidth();

    // Full image width
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(pictureFile.getAbsolutePath(), options);
    int imageWidth = options.outWidth;
    if (imageWidth == 0 || screenWidth == 0) {
      Toast.makeText(this, getResources().getString(R.string.unexpected_error).toString(), Toast.LENGTH_LONG).show();
      return;
    }

    // Ratio
    int ratio = Math.max(1, (int)(imageWidth / screenWidth));

    // Decode scaled down image, display it
    options.inJustDecodeBounds = false;
    options.inSampleSize = ratio;
    Bitmap image = BitmapFactory.decodeFile(pictureFile.getAbsolutePath(), options);
    
    ImageView viewPicture = (ImageView)findViewById(R.id.viewPicture);
    if (viewPicture != null) {
      viewPicture.setImageBitmap(image);
    }
  }

  private void handleDelete() {
    //Display a warning
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(getResources().getString(R.string.confirm_delete_message))
    .setCancelable(false)
    .setPositiveButton(getResources().getString(R.string.confirm_delete_yes), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        doDelete();
      }
    })
    .setNegativeButton(getResources().getString(R.string.confirm_delete_no), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        dialog.cancel();
      }
    });
    AlertDialog alert = builder.create();
    alert.show();
  }
  
	private void doDelete() {
		int deleteResult = DatabaseAdapter.instance().delete(vin);
		if (deleteResult == 1) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.wine_delete_success),Toast.LENGTH_SHORT).show();
			
	     // Delete the image, if it exists
      if (imageFile != null) {
        if (imageFile.exists()) {
          imageFile.delete();
          imageFile = null;
        }
      }
			
			setResult(Constants.RETURN_DELETE_OK);
		}
		else {
			//This should never happen as id is the primary key
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.wine_delete_error),Toast.LENGTH_SHORT).show();
			setResult(RESULT_CANCELED);
		}
		this.finish();
	}
	
	private void doUpdate() {
		//Start an EditWine Activity
		Bundle bundle = new Bundle();
		bundle.putSerializable(Constants.DATA_WINE, vin);
		Intent newIntent = new Intent(getApplicationContext(), EditScreen.class);
		newIntent.putExtras(bundle);
		startActivityForResult(newIntent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			//Update success case -> update info
		  if (data == null || data.getSerializableExtra(Constants.DATA_WINE) == null || !(data.getSerializableExtra(Constants.DATA_WINE) instanceof Vin)) {
		    Toast.makeText(this, getResources().getString(R.string.unexpected_error).toString(), Toast.LENGTH_LONG).show();
		    return;
		  }
		  vin = (Vin)data.getSerializableExtra(Constants.DATA_WINE);
			initFields();
		}
		else if (resultCode == Constants.RETURN_DELETE_OK) {
			//Delete success case -> forward the info to the ListView
			setResult(Constants.RETURN_DELETE_OK);
			this.finish();
		}
		else if (resultCode == Constants.RETURN_HOME) {
		  setResult(Constants.RETURN_HOME);
		  finish();
		}
	}
	
	@Override
	public void onBackPressed() {
	  Intent data = new Intent();
	  data.putExtra(Constants.DATA_WINE, vin);
	  setResult(Constants.RETURN_CANCEL, data);
	  super.onBackPressed();
	}
	
	 // Menu-related
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.display_screen_menu, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      // Handle item selection
      if (item.getItemId() == R.id.menu_edit) {
        doUpdate();
      }
      else if (item.getItemId() == R.id.menu_delete) {
        handleDelete();
      }
      else if (item.getItemId() == android.R.id.home) {
        setResult(Constants.RETURN_HOME);
        finish();
      }
      else if (item.getItemId() == R.id.menu_drink) {
        handleDrink();
      }
      return true;
  }
  // Menu ends
  
  
  private void handleDrink() {
    if (vin.getStock() > 0) {
      // Show up the confirm dialog
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage(getResources().getString(R.string.confirm_drink_message))
      .setCancelable(false)
      .setPositiveButton(getResources().getString(R.string.confirm_delete_yes), new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          doDrink();
        }
      })
      .setNegativeButton(getResources().getString(R.string.confirm_delete_no), new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          dialog.cancel();
        }
      });
      AlertDialog alert = builder.create();
      alert.show();
    }
    else {
      Toast.makeText(getApplicationContext(), getResources().getString(R.string.drink_not_enough), Toast.LENGTH_SHORT).show();
    }
  }
   

  private void doDrink() {
    try {
      if (DatabaseAdapter.instance().setStock(vin.getId(), vin.getStock() -1)) {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.drink_ok), Toast.LENGTH_SHORT).show();
        vin.setStock(vin.getStock() -1);
        initFields();
      }
      else {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
      }
    }
    catch (Exception e) {
      Toast.makeText(getApplicationContext(), getResources().getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
    }
  }
  
  
  
  /**
   * Retrieves the shared preferences and reads them to show/hide components. By default, show everything
   */
  protected void processPreferences() {
    SharedPreferences prefs = getSharedPreferences(Constants.PREF, MODE_PRIVATE);
    boolean showPictures = prefs.getBoolean(Constants.PREF_PICTURE, true);
    boolean showAging = prefs.getBoolean(Constants.PREF_AGING, true);
    boolean showStock = prefs.getBoolean(Constants.PREF_STOCK, true);
    boolean showPosPrice = prefs.getBoolean(Constants.PREF_POS_AND_PRICE, true);
    boolean showVariety = prefs.getBoolean(Constants.PREF_VARIETY, true);
    boolean showAssessments = prefs.getBoolean(Constants.PREF_ASSESSMENTS, true);
    boolean showBestWith = prefs.getBoolean(Constants.PREF_BEST_WITH, true);
    
    LinearLayout layoutAging = ((LinearLayout)findViewById(R.id.panelGarde)),
                 layoutPos = ((LinearLayout)findViewById(R.id.panelPOS)),
                 layoutPrix = ((LinearLayout)findViewById(R.id.panelPrix)),
                 viewAccords       = ((LinearLayout)findViewById(R.id.panelAccords)),
                 viewDescription   = ((LinearLayout)findViewById(R.id.panelDescription)),
                 viewVarieties     = ((LinearLayout)findViewById(R.id.panelCepage));
    TextView viewStock = ((TextView)findViewById(R.id.inputStock));
    
    ImageView viewPicture = (ImageView)findViewById(R.id.viewPicture);
    
    if (!showPictures) {
      viewPicture.setVisibility(View.GONE);
    }

    if (!showAging) {
      layoutAging.setVisibility(View.GONE);
    }
    if (!showStock) {
      viewStock.setVisibility(View.GONE);
    }
    if (!showPosPrice) {
      layoutPos.setVisibility(View.GONE);
      layoutPrix.setVisibility(View.GONE);
    }
    if (!showVariety) {
      viewVarieties.setVisibility(View.GONE);
    }
    if (!showAssessments) {
      viewDescription.setVisibility(View.GONE);
    }
    if (!showBestWith) {
      viewAccords.setVisibility(View.GONE);
    }
    
  }
}
