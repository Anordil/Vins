package com.devilopers.guigeek.vins;

import java.io.File;
import java.io.Serializable;
import java.util.Currency;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guigeek.vins.R;

public class EditScreen extends AddScreen {
	
	private Vin vin;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tvClassification.setOnClickListener(this);
    tvCellar.setOnClickListener(this);
    tvAssessment.setOnClickListener(this);

		Bundle bundle = this.getIntent().getExtras();
		Serializable data = bundle.getSerializable(Constants.DATA_WINE);
    if (data == null || !(data instanceof Vin)) {
      Toast.makeText(getApplicationContext(), getResources().getString(R.string.open_edit_error),Toast.LENGTH_SHORT).show();
      this.finish();
    }
    vin = (Vin)data;

		_addButton.setVisibility(View.GONE);

		_editButton.setOnClickListener(this);
		_editButton.setVisibility(View.VISIBLE);

		_stockInc.setOnClickListener(this);
		_stockInc.setVisibility(View.VISIBLE);
		_stockDec.setOnClickListener(this);
		_stockDec.setVisibility(View.VISIBLE);

		processPreferences();
		initFields();
	}
	
	private void initFields() {
		TextView 
			viewNom 			= ((TextView)findViewById(R.id.inputNom)),
			viewAppellation 	= ((TextView)findViewById(R.id.inputAppellation)),
			viewMillesime 		= ((TextView)findViewById(R.id.inputMillesime)),
			viewCepage 			= ((TextView)findViewById(R.id.inputCepage)),
			viewAccords 		= ((TextView)findViewById(R.id.inputAccords)),
			viewDescription 	= ((TextView)findViewById(R.id.inputDescription)),
			viewPrix 			= ((TextView)findViewById(R.id.inputPrix)),
			viewLieu 			= ((TextView)findViewById(R.id.inputPointOfSale)),
			viewAging 			= ((TextView)findViewById(R.id.inputGarde)),
			viewStock 			= ((TextView)findViewById(R.id.inputStock)),
			viewCurrency		= ((TextView)findViewById(R.id.currency));
		RatingBar 
			viewNote  			= ((RatingBar)findViewById(R.id.input_note));
		
		viewNom.setText(vin.getNom());
		viewAppellation.setText(vin.getAppellation());
		viewMillesime.setText(String.valueOf(vin.getMillesime()));
		viewCepage.setText(vin.getCepage());
		viewAccords.setText(vin.getAccords());
		viewDescription.setText(vin.getDescription());
		viewPrix.setText(String.valueOf(vin.getPrice()));
		viewNote.setRating(vin.getNote());
		viewLieu.setText(vin.getPointOfSale());
		viewAging.setText(String.valueOf(vin.getAgingPotential()));
		viewStock.setText(String.valueOf(vin.getStock()));
		compartmentId = vin.getLocation();
		
		if (vin.getLocation() != 0) {
		  tvLocation.setText(DatabaseAdapter.instance().getCompartmentLongName(vin.getLocation()));
		} else {
		  tvLocation.setText(getResources().getString(R.string.no_location));
		}
		
		// Don't display a quantity if it's set to 0
		if (viewStock.getText().length() == 1 && viewStock.getText().charAt(0) == '0') {
			viewStock.setText("");
		}
		
		// Don't fill in the vintage if it's set to 0
		if (viewMillesime.getText().length() == 1 && viewMillesime.getText().charAt(0) == '0') {
		  viewMillesime.setText("");
    }
		
		viewCurrency.setText(Currency.getInstance(Locale.getDefault()).getSymbol());
		
		
		String colour = vin.getColour();
		if (colour.equals(DatabaseAdapter.COLOUR_RED)) {
			_spinner.setSelection(0);
		}
		if (colour.equals(DatabaseAdapter.COLOUR_WHITE)){
			_spinner.setSelection(1);
		}
		if (colour.equals(DatabaseAdapter.COLOUR_ROSE)) {
			_spinner.setSelection(2);
		}
		if (colour.equals(DatabaseAdapter.COLOUR_YELLOW)) {
			_spinner.setSelection(3);
		}
		if (colour.equals(DatabaseAdapter.COLOUR_CHAMPAGNE)) {
			_spinner.setSelection(4);
		}
		if (colour.equals(DatabaseAdapter.COLOUR_FORTIFIED)) {
			_spinner.setSelection(5);
		}
		
		// Image
		String imagePath = vin.getImagePath();
		if (imagePath != null && imagePath.length() > 0) {
		  pictureFile = new File(imagePath);
		  if (!pictureFile.exists()) {
		    pictureFile = null;
		  }
		  else {
		    displayNewPicture();
		  }
		}
	}
	
	@Override
 	public boolean checkData() {
		
		TextView 
			nom = ((TextView)findViewById(R.id.inputNom)),
			appellation = ((TextView)findViewById(R.id.inputAppellation)),
			millesime = ((TextView)findViewById(R.id.inputMillesime));
		
		//Empty error fields
		nom.setError(null);
		appellation.setError(null);
		millesime.setError(null);
		
		if ( nom.getText().length() < 1 ) {
			nom.setError(getResources().getString(R.string.mandatory_field_erorr));
			return false;
		}
		if ( appellation.getText().length() < 1 ) {
			appellation.setError(getResources().getString(R.string.mandatory_field_erorr));
			return false;
		}
		if ( millesime.getText().length() != 4 && millesime.getText().length() != 0 ) {
			millesime.setError(getResources().getString(R.string.invalid_date_format_erorr));
			return false;
		}
		
		return true;
	}

	@Override
	// Add a new item in the DB if all mandatory fields are filled
	public void onClick(View v) {
	  
		if (v == _editButton) {
			doUpdate();
		}
		else if (v == _stockInc) {
			TextView viewStock = ((TextView)findViewById(R.id.inputStock));
			String stockString = viewStock.getText().toString();
			if (stockString.length() == 0) { stockString = "0"; }
			
			int stock;
			try { stock = Integer.parseInt(stockString); }
			catch (Exception e) { stock = 0; }
			
			stock++;
			viewStock.setText(String.valueOf(stock));
			_stockDec.setEnabled(true);
		}
		else if (v == _stockDec) {
			TextView viewStock = ((TextView)findViewById(R.id.inputStock));
			String stockString = viewStock.getText().toString();
			if (stockString.length() == 0) { stockString = "0"; }
			
			int stock;
			try { stock = Integer.parseInt(stockString); }
			catch (Exception e) { stock = 0; }
			
			stock--;
			if (stock >= 0) {
				viewStock.setText(String.valueOf(stock));
			}
			_stockDec.setEnabled(stock > 0);
		}
		// Parent handles hide/show
    else {
      super.onClick(v);
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
			if (pictureFile != null) {
			  if (pictureFile.exists()) {
			    pictureFile.delete();
			    pictureFile = null;
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
		// Check mandatory fields:
		if (!checkData()) {
			return;
		}
		
		TextView 
			viewNom 			= ((TextView)findViewById(R.id.inputNom)),
			viewAppellation 	= ((TextView)findViewById(R.id.inputAppellation)),
			viewMillesime 		= ((TextView)findViewById(R.id.inputMillesime)),
			viewCepage 			= ((TextView)findViewById(R.id.inputCepage)),
			viewAccords 		= ((TextView)findViewById(R.id.inputAccords)),
			viewDescription 	= ((TextView)findViewById(R.id.inputDescription)),
			viewPrix 			= ((TextView)findViewById(R.id.inputPrix)),
			viewLieu 			= ((TextView)findViewById(R.id.inputPointOfSale)),
			viewAging 			= ((TextView)findViewById(R.id.inputGarde)),
			viewStock 			= ((TextView)findViewById(R.id.inputStock));
		RatingBar 
			viewNote  			= ((RatingBar)findViewById(R.id.input_note));
		
		// Set vintage to 0 if it hasn't been filled in
		if (viewMillesime.getText().length() == 0) {
			viewMillesime.setText("0");
		}
		
		String colour;
		switch (_spinner.getSelectedItemPosition()) {
		default:
			colour=DatabaseAdapter.COLOUR_RED;
			break;
		case 0:
			colour=DatabaseAdapter.COLOUR_RED;
			break;
		case 1:
			colour=DatabaseAdapter.COLOUR_WHITE;
			break;
		case 2:
			colour=DatabaseAdapter.COLOUR_ROSE;
			break;
		case 3:
			colour=DatabaseAdapter.COLOUR_YELLOW;
			break;
		case 4:
			colour=DatabaseAdapter.COLOUR_CHAMPAGNE;
			break;
		case 5:
			colour=DatabaseAdapter.COLOUR_FORTIFIED;
			break;
		}
		
		
    vin.setNom(viewNom.getText().toString());
    vin.setAppellation(viewAppellation.getText().toString());
    vin.setColour(colour);
    vin.setCepage(viewCepage.getText().toString());
    vin.setAccords(viewAccords.getText().toString());
    vin.setDescription(viewDescription.getText().toString());
    vin.setMillesime(Integer.parseInt(viewMillesime.getText().toString()));
    vin.setNote((int)viewNote.getRating());
    vin.setPrice(Double.parseDouble( "0" + viewPrix.getText().toString() ));
    vin.setPointOfSale(viewLieu.getText().toString());
    vin.setAgingPotential(Integer.parseInt("0" + viewAging.getText().toString()));
    vin.setStock(Integer.parseInt("0" + viewStock.getText().toString()));
    vin.setImagePath(pictureFile == null ? null : pictureFile.getAbsolutePath());
    vin.setLocation(compartmentId);
		
		boolean updateResult = DatabaseAdapter.instance().updateEntry(vin); 
		
		if (!updateResult) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.wine_updated_error),Toast.LENGTH_SHORT).show();
			setResult(RESULT_CANCELED);
		}
		else {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.wine_updated_message), Toast.LENGTH_SHORT).show();
			Intent result = new Intent();
			result.putExtra(Constants.DATA_WINE, vin);
			setResult(RESULT_OK, result);
		}
				
		this.finish();
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
    inflater.inflate(R.menu.delete_home_menu, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    if (item.getItemId() == R.id.menu_delete) {
      handleDelete();
    }
    else if (item.getItemId() == R.id.menu_home) {
      setResult(Constants.RETURN_HOME);
      finish();
    }
    return true;
  }
  // Menu ends
}
