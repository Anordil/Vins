package com.devilopers.guigeek.vins;

import java.util.Calendar;
import java.util.Vector;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.guigeek.vins.R;

public class SearchScreen extends Activity  implements OnClickListener {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        
        ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
        
        Button searchButton = ( (Button)findViewById(R.id.searchButton) );
        searchButton.setOnClickListener(this);
        
        // Init spinners
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( this, R.array.sign_array, android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinnerVintage = (Spinner) findViewById(R.id.spinnerMillesime);
        spinnerVintage.setAdapter(adapter);
        spinnerVintage.setSelection(1);
        
        Spinner spinnerAging = (Spinner) findViewById(R.id.spinnerAging);
        spinnerAging.setAdapter(adapter);
        spinnerAging.setSelection(1);
        
        Spinner spinnerPrice = (Spinner) findViewById(R.id.spinnerPrice);
        spinnerPrice.setAdapter(adapter);
        spinnerPrice.setSelection(1);
        
        Spinner spinnerStock = (Spinner) findViewById(R.id.spinnerStock);
        spinnerStock.setAdapter(adapter);
        spinnerStock.setSelection(2);
        
        // Completion adapters
        AutoCompleteTextView pointOfSaleTV = (AutoCompleteTextView)findViewById(R.id.inputPOS);
        AutoCompleteTextView nameTV = (AutoCompleteTextView)findViewById(R.id.inputNom);
        AutoCompleteTextView appellationTV = (AutoCompleteTextView)findViewById(R.id.inputAppellation);
        AutoCompleteTextView cepagesTV = (AutoCompleteTextView)findViewById(R.id.inputCepage);
        AutoCompleteTextView accordsTV = (AutoCompleteTextView)findViewById(R.id.inputAccords);
        
        DistinctDataAdapter aDistinctDataAdapter = new DistinctDataAdapter(this);
        ArrayAdapter<String> posAdapter = aDistinctDataAdapter.getAdapter(DatabaseAdapter.KEY_POINT_OF_SALE);
        ArrayAdapter<String> nameAdapter = aDistinctDataAdapter.getAdapter(DatabaseAdapter.KEY_NOM);
        ArrayAdapter<String> appellationAdapter = aDistinctDataAdapter.getAdapter(DatabaseAdapter.KEY_APPELLATION);
        ArrayAdapter<String> cepageAdapter = aDistinctDataAdapter.getAdapter(DatabaseAdapter.KEY_CEPAGE, true);
        ArrayAdapter<String> accordsAdapter = aDistinctDataAdapter.getAdapter(DatabaseAdapter.KEY_ACCORDS, true);
        
        if (posAdapter != null) {
          pointOfSaleTV.setAdapter(posAdapter);
        }
        if (nameAdapter != null) {
          nameTV.setAdapter(nameAdapter);
        }
        if (appellationAdapter != null) {
          appellationTV.setAdapter(appellationAdapter);
        }
        if (cepageAdapter != null) {
          cepagesTV.setAdapter(cepageAdapter);
        }
        if (accordsAdapter != null) {
          accordsTV.setAdapter(accordsAdapter);
        }
    }

	// Checks that the date is a 4-digit one (if it has ben provided)
	public boolean checkData() {
		TextView millesime = ((TextView)findViewById(R.id.inputMillesime));
		
		//Empty error field
		millesime.setError(null);
		
		if ( millesime.getText().length() > 1 && millesime.getText().length() != 4 ) {
			millesime.setError(getResources().getString(R.string.invalid_date_format_erorr));
			return false;
		}
		
		return true;
	}
	
	// Puts the data that has been filled in a ContentValues field that will be used to build the SQL query
	public ContentValues getData() {
		TextView 
			nom = 			((TextView)findViewById(R.id.inputNom)),
			appellation = 	((TextView)findViewById(R.id.inputAppellation)),
			millesime = 	((TextView)findViewById(R.id.inputMillesime)),
			cepages = 		((TextView)findViewById(R.id.inputCepage)),
			accords = 		((TextView)findViewById(R.id.inputAccords)),
			pos = 			((TextView)findViewById(R.id.inputPOS)),
			prix = 			((TextView)findViewById(R.id.inputPrix)),
			stock = 	  ((TextView)findViewById(R.id.inputStock)),
			aging =     ((TextView)findViewById(R.id.inputAging));
		Spinner
			spinnerMillesime = 	(Spinner)findViewById(R.id.spinnerMillesime),
			spinnerAging =   (Spinner)findViewById(R.id.spinnerAging),
			spinnerPrix = 		(Spinner)findViewById(R.id.spinnerPrice),
			spinnerStock = 		(Spinner)findViewById(R.id.spinnerStock);
		
		CheckBox 
			red = 			((CheckBox)findViewById(R.id.checkbox_red)),
			rose = 			((CheckBox)findViewById(R.id.checkbox_rose)),
			white = 		((CheckBox)findViewById(R.id.checkbox_white)),
			yellow = 		((CheckBox)findViewById(R.id.checkbox_yellow)),
			champagne =     ((CheckBox)findViewById(R.id.checkbox_champagne)),
			fortified =     ((CheckBox)findViewById(R.id.checkbox_fortified));
		
		ContentValues values = new ContentValues();
		
		if (nom.getText().length() > 0) {
			values.put(DatabaseAdapter.KEY_NOM, nom.getText().toString().replace("'", "''"));
		}
		if (appellation.getText().length() > 0) {
			values.put(DatabaseAdapter.KEY_APPELLATION, appellation.getText().toString().replace("'", "''"));
		}
		if (cepages.getText().length() > 0) {
			values.put(DatabaseAdapter.KEY_CEPAGE, cepages.getText().toString().replace("'", "''"));
		}
		if (accords.getText().length() > 0) {
			values.put(DatabaseAdapter.KEY_ACCORDS, accords.getText().toString().replace("'", "''"));
		}
		
		if (pos.getText().length() > 0) {
			values.put(DatabaseAdapter.KEY_POINT_OF_SALE, pos.getText().toString().replace("'", "''"));
		}
		
		if (millesime.getText().length() > 0) {
			values.put(DatabaseAdapter.KEY_MILLESIME, millesime.getText().toString().replace("'", "''"));
			values.put(DatabaseAdapter.MILLESIME_COMPARATOR, (String)spinnerMillesime.getSelectedItem());
		}
		if (prix.getText().length() > 0) {
			values.put(DatabaseAdapter.KEY_PRIX, prix.getText().toString().replace("'", "''"));
			values.put(DatabaseAdapter.PRICE_COMPARATOR, (String)spinnerPrix.getSelectedItem());
		}
		if (stock.getText().length() > 0) {
			values.put(DatabaseAdapter.KEY_STOCK, stock.getText().toString().replace("'", "''"));
			values.put(DatabaseAdapter.STOCK_COMPARATOR, (String)spinnerStock.getSelectedItem());
		}
		
		if (aging.getText().length() > 0) {
		  int targetYear = Calendar.getInstance().get(Calendar.YEAR) + Integer.parseInt(aging.getText().toString());
		  values.put(DatabaseAdapter.KEY_AGING_POTENTIAL, targetYear);
      values.put(DatabaseAdapter.AGING_COMPARATOR, (String)spinnerAging.getSelectedItem());
		}

		values.put(DatabaseAdapter.COLOUR_RED, red.isChecked());
		values.put(DatabaseAdapter.COLOUR_ROSE, rose.isChecked());
		values.put(DatabaseAdapter.COLOUR_WHITE, white.isChecked());
		values.put(DatabaseAdapter.COLOUR_YELLOW, yellow.isChecked());
		values.put(DatabaseAdapter.COLOUR_CHAMPAGNE, champagne.isChecked());
		values.put(DatabaseAdapter.COLOUR_FORTIFIED, fortified.isChecked());
		
		return values;
	}
	
	@Override
	public void onClick(View v) {
		// check date format
		if (!checkData()) {
			return;
		}
		
		Vector<Vin> results = DatabaseAdapter.instance().search(getData());
		if (results.size() > 0) {
			Toast.makeText(getApplicationContext(), results.size() + " " + getResources().getString(R.string.x_matches), Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(SearchScreen.this, WineListView.class);
			intent.putExtra(Constants.DATA_WINE_VECTOR, new WineVectorSerializer(results));
			startActivity(intent);
			this.finish();
		}
		else {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_result_message), Toast.LENGTH_SHORT).show();
		}
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return true;
	}
	
}
