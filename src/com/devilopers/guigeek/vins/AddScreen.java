package com.devilopers.guigeek.vins;

import java.io.File;
import java.io.FileOutputStream;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.guigeek.vins.R;

public class AddScreen extends Activity implements OnClickListener {
	
	protected Button _addButton, _editButton, _stockInc, _stockDec, _buttonPicture, _buttonPictureLoad, _buttonLocation;
	protected Spinner _spinner;
	protected MultiAutoCompleteTextView _cepagesTV, _accordsTV;
	protected ImageView _viewPicture;
	protected File pictureFile = null;
	protected int compartmentId = 0;
	
	protected LinearLayout layClassification, layCellar, layAssessment;
	protected TextView tvClassification, tvCellar, tvAssessment, tvLocation;
	protected LayoutTransition mTransitioner;
	
	public static final String K_MULTIPLE_VALUES_TOKENIZER = ", ";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add);
		
		
		
		layClassification = (LinearLayout)findViewById(R.id.layout_classification);
		layCellar = (LinearLayout)findViewById(R.id.layout_cave_mgt);
		layAssessment = (LinearLayout)findViewById(R.id.layout_assessment);
		
		tvClassification = (TextView)findViewById(R.id.tvClassification);
		tvCellar = (TextView)findViewById(R.id.labelCellarMgt);
		tvAssessment = (TextView)findViewById(R.id.tvAssessment);
		tvLocation = (TextView)findViewById(R.id.labelLocation);
		tvLocation.setText(getResources().getString(R.string.no_location));
		
		tvClassification.setOnClickListener(this);
    tvCellar.setOnClickListener(this);
    tvAssessment.setOnClickListener(this);

		_addButton = ( (Button)findViewById(R.id.addButton) );
		_addButton.setOnClickListener(this);


		_editButton = ( (Button)findViewById(R.id.editButton) );
		_editButton.setVisibility(View.GONE);
		
		_buttonPicture = (Button)findViewById(R.id.buttonPicture);
		_buttonPicture.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        PictureManager pm = new PictureManager(AddScreen.this);
        pictureFile = pm.startCamera(pictureFile);
      }
		});
		
		_buttonPictureLoad = (Button)findViewById(R.id.buttonPictureLoad);
		_buttonPictureLoad.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        PictureManager pm = new PictureManager(AddScreen.this);
        pictureFile = pm.browseToFile(pictureFile);
      }
    });
		
		_buttonLocation = (Button)findViewById(R.id.selectLocation);
		_buttonLocation.setOnClickListener(this);
		
		_viewPicture = (ImageView)findViewById(R.id.viewPicture);
		
		_stockInc = ( (Button)findViewById(R.id.stockIncrease) );
		_stockInc.setVisibility(View.GONE);
		_stockDec = ( (Button)findViewById(R.id.stockDecrease) );
		_stockDec.setVisibility(View.GONE);

		_spinner = (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.colour_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		_spinner.setAdapter(adapter);
		
		// Custom dictionaries for name / appellation / point of sale
		AutoCompleteTextView pointOfSaleTV = (AutoCompleteTextView)findViewById(R.id.inputPointOfSale);
		AutoCompleteTextView nameTV = (AutoCompleteTextView)findViewById(R.id.inputNom);
		AutoCompleteTextView appellationTV = (AutoCompleteTextView)findViewById(R.id.inputAppellation);
		
		DistinctDataAdapter aDistinctDataAdapter = new DistinctDataAdapter(this);
		ArrayAdapter<String> posAdapter = aDistinctDataAdapter.getAdapter(DatabaseAdapter.KEY_POINT_OF_SALE);
		ArrayAdapter<String> nameAdapter = aDistinctDataAdapter.getAdapter(DatabaseAdapter.KEY_NOM);
		ArrayAdapter<String> appellationAdapter = aDistinctDataAdapter.getAdapter(DatabaseAdapter.KEY_APPELLATION);
		
		if (posAdapter != null) {
		  pointOfSaleTV.setAdapter(posAdapter);
		}
		if (nameAdapter != null) {
		  nameTV.setAdapter(nameAdapter);
    }
		if (appellationAdapter != null) {
		  appellationTV.setAdapter(appellationAdapter);
    }
		
		// Same for variety and best served with, but using a multi auto complete text view
		_cepagesTV = ((MultiAutoCompleteTextView)findViewById(R.id.inputCepage));
		ArrayAdapter<String> cepagesAdapter = aDistinctDataAdapter.getAdapter(DatabaseAdapter.KEY_CEPAGE, true);
		if (cepagesAdapter != null) {
		  _cepagesTV.setAdapter(cepagesAdapter);
		  _cepagesTV.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		  _cepagesTV.setOnFocusChangeListener(new TrimOnFocusLossListener());
		}
		
		_accordsTV = ((MultiAutoCompleteTextView)findViewById(R.id.inputAccords));
    ArrayAdapter<String> accordsAdapter = aDistinctDataAdapter.getAdapter(DatabaseAdapter.KEY_ACCORDS, true);
    if (accordsAdapter != null) {
      _accordsTV.setAdapter(accordsAdapter);
      _accordsTV.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
      _accordsTV.setOnFocusChangeListener(new TrimOnFocusLossListener());
    }
    
    processPreferences();
	}
	
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
	  
	  LinearLayout lay = null;
	  
		if (v == _addButton) {
			doAdd();
		}
		else if (v == _buttonLocation) {
		  doSelectLocation();
		}
		else if (v == tvClassification) {
		  lay = layClassification;
		}
    else if (v == tvCellar) {
      lay = layCellar;
    }
    else if (v == tvAssessment) {
      lay = layAssessment;
    }
		
		if (lay != null) {
		  if (lay.getVisibility() == View.GONE) {
		    lay.setVisibility(View.VISIBLE);
		    lay.animate().scaleY(1).setListener(null);
		  }
		  else {
		    final LinearLayout datLayout = lay;
		    lay.animate().scaleY(0).setListener(new AnimatorListener() {
          public void onAnimationEnd(Animator animation) {
            datLayout.setVisibility(View.GONE);
          }
          
          public void onAnimationCancel(Animator animation) { }
          public void onAnimationRepeat(Animator animation) { }
          public void onAnimationStart(Animator animation) { }
		    });
		  }
		}
	}
	
	public void doAdd() {
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
		
		// Set vintage to 0 if it hasn't been filled in
		if (viewMillesime.getText().length() == 0) {
			viewMillesime.setText("0");
		}
		
		long addResult = DatabaseAdapter.instance().addEntry(
				viewNom.getText().toString(),
				viewAppellation.getText().toString(),
				colour,
				viewCepage.getText().toString(),
				viewAccords.getText().toString(),
				viewDescription.getText().toString(),
				Integer.parseInt(viewMillesime.getText().toString()),
				(int)viewNote.getRating(),
				Double.parseDouble( "0" + viewPrix.getText().toString() ),    //Parsing an empty string would cause an error
				viewLieu.getText().toString(),
				Integer.parseInt("0" + viewAging.getText().toString()),
				Integer.parseInt("0" + viewStock.getText().toString()),
				pictureFile == null ? null : pictureFile.getAbsolutePath(),
				compartmentId);
		
		if (addResult == -1) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.wine_added_error),Toast.LENGTH_SHORT).show();
		}
		else if (addResult == -2) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.already_exists_error),Toast.LENGTH_LONG).show();
		}
		else {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.wine_added_message), Toast.LENGTH_SHORT).show();
		}
				
		this.finish();
	}
	
	protected void doSelectLocation() {
    Intent newIntent = new Intent(getApplicationContext(), SectionScreen.class);
    startActivityForResult(newIntent, Constants.REQUEST_SELECT_LOCATION);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  if (requestCode == Constants.REQUEST_PICTURE) {
	    if (resultCode != Activity.RESULT_OK) {
	      Toast.makeText(this, getResources().getString(R.string.picture_error).toString(), Toast.LENGTH_LONG).show();
	      pictureFile = null;
	      return;
	    }
	    
	    // At this point, pictureFile should point to the picture that has just been taken. Assert, just in case.
	    if (pictureFile == null) {
	      Toast.makeText(this, getResources().getString(R.string.picture_error).toString(), Toast.LENGTH_LONG).show();
	      return;
	    }
	    
	     // All is good - Picture successfully saved
      Toast.makeText(this, getResources().getString(R.string.picture_ok).toString(), Toast.LENGTH_LONG).show();
	    
      // Recycling the old picture if any so as to free memory
	    if (_viewPicture.getDrawable() != null) {
	      Bitmap oldBitmap = ((BitmapDrawable)_viewPicture.getDrawable()).getBitmap();
	      if (oldBitmap != null) {
	        oldBitmap.recycle();
	      }
	    }
	    
	    // Display the new picture
	    displayNewPicture();
	  }
	  else if (requestCode == Constants.REQUEST_BROWSE_PHOTO) {
	    if (resultCode != Activity.RESULT_OK) {
	      Toast.makeText(this, getResources().getString(R.string.picture_error).toString(), Toast.LENGTH_LONG).show();
	      pictureFile = null;
	      return;
	    }
	    else {
	      // Save the picture in the same folder as the others
	      Uri selectedImage = data.getData();
	      if (!decodeAndSave(selectedImage, pictureFile)) {
	        Toast.makeText(this, getResources().getString(R.string.unexpected_error).toString(), Toast.LENGTH_LONG).show();
	        pictureFile = null;
	        return;
	      }

	      // Recycling the old picture if any so as to free memory
	      if (_viewPicture.getDrawable() != null) {
	        Bitmap oldBitmap = ((BitmapDrawable)_viewPicture.getDrawable()).getBitmap();
	        if (oldBitmap != null) {
	          oldBitmap.recycle();
	        }
	      }

	      // Display the new picture
	      displayNewPicture();
	    }
	  }
	  else if (requestCode == Constants.REQUEST_SELECT_LOCATION) {
	    if (resultCode == Activity.RESULT_OK) {
	      compartmentId = data.getExtras().getInt(Constants.DATA_COMP_ID);
	      if (compartmentId != 0) {
	        // Display the location's name
	        tvLocation.setText(DatabaseAdapter.instance().getCompartmentLongName(compartmentId));
	      } else {
	        tvLocation.setText(getResources().getString(R.string.no_location));
	      }
	    }
	  }
	}
	
	private boolean decodeAndSave(Uri selectedImage, File pictureFile) {
	  try {
	    if (pictureFile == null || _viewPicture == null) {
	      return false;
	    }

	    // Screen width
	    Display d = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	    int screenWidth = d.getWidth();

	    // Full image width
	    BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, options);
	    int imageWidth = options.outWidth;
	    if (imageWidth == 0 || screenWidth == 0) {
	      return false;
	    }

	    // Ratio
	    int ratio = Math.max(1, (int)(imageWidth / screenWidth));

	    // Decode scaled down image
	    options.inJustDecodeBounds = false;
	    options.inSampleSize = ratio;
	    Bitmap image = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, options);
	    
	    // Save it
	    FileOutputStream out = new FileOutputStream(pictureFile);
	    image.compress(Bitmap.CompressFormat.JPEG, 100, out);
      out.close();
	  }
	  catch (Exception e) {
	    return false;
	  }

	  return true;
	}

  // Gets the new image's size, decodes a scaled down version and displays it in _viewPicture
	protected void displayNewPicture() {
	  if (pictureFile == null || _viewPicture == null) {
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
	  _viewPicture.setImageBitmap(image);
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
	  
	  LinearLayout layoutPicture = ((LinearLayout)findViewById(R.id.layoutPictureButtons)),
                 layoutPos     = ((LinearLayout)findViewById(R.id.layoutPosPrice)),
                 layoutAging   = ((LinearLayout)findViewById(R.id.layoutAging)),
                 layoutStock   = ((LinearLayout)findViewById(R.id.layoutStock));
	  
	  TextView viewAccords       = ((TextView)findViewById(R.id.inputAccords)),
	           viewDescription   = ((TextView)findViewById(R.id.inputDescription)),
	           viewVarieties     = ((TextView)findViewById(R.id.inputCepage)),
	           labelCellarMgt    = ((TextView)findViewById(R.id.labelCellarMgt));
	  
	  if (!showPictures) {
	    layoutPicture.setVisibility(View.GONE);
	    _viewPicture.setVisibility(View.GONE);
	  }

	  if (!showAging) {
	    layoutAging.setVisibility(View.GONE);
	  }
	  if (!showStock) {
	    layoutStock.setVisibility(View.GONE);
	  }
	  if (!showPosPrice) {
	    layoutPos.setVisibility(View.GONE);
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
	  
	  // Hide the whole "cave management" part if there's nothing in it
	  if (!showAging && !showStock && !showPosPrice) {
	    labelCellarMgt.setVisibility(View.GONE);
	  }
	}

  // MultipleAutoCompleteTextView automatically adds ", " after each token, and the last comma cannot be removed.
	// Programmatically remove it when the text view loses focus
	public class TrimOnFocusLossListener implements OnFocusChangeListener {
	  // Trim text (remove the last ", ") when focus is lost
	  public void onFocusChange(View iView, boolean bHasFocus) {
	    if (bHasFocus == false) {
	      if (_cepagesTV == iView || _accordsTV == iView) {
	        TextView theTextView = (TextView)iView;
	        String theText = theTextView.getText().toString();
	        if (theText.endsWith(K_MULTIPLE_VALUES_TOKENIZER)) {
	          theTextView.setText(theText.substring(0, theText.length() - K_MULTIPLE_VALUES_TOKENIZER.length()));
	        }
	      }
	    }
	  }
	}
}
