package com.devilopers.guigeek.vins;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import Vins.R;

public class WineListView extends ListActivity {
	
	private Vin[] _wineList;
	private Vin[] _wineListFiltered;
	private HashMap<String, Vin> _IDsMap;
	private HashMap<Vin, String> _winesMap;
	private Vin updatedItem = null;
	
	private ListView listView = null;
	private int scrollXOffset = 0;
	private int scrollY = -1;
	
	protected EditText filterText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_activity);
		
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		
		filterText = (EditText)findViewById(R.id.filterText);
		filterText.setOnEditorActionListener(new FilterListener());
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		
		listView = (ListView)findViewById(android.R.id.list);
		
		registerForContextMenu(getListView());
		
    final Object data = getLastNonConfigurationInstance();
    
    // first init
    if (data == null) {
      _IDsMap = new HashMap<String, Vin>();
      _winesMap = new HashMap<Vin, String>();
      init();
    }
    // Orientation change
    else if (data instanceof DataStore) {
      DataStore ds = (DataStore)data;
      _wineList = ds._dswineList;
      _IDsMap = ds._dsIDsMap;
      _winesMap = ds._dswinesMap;
      updatedItem = ds.dsupdatedItem;
    }
    // Error
    else {
      Toast.makeText(getApplicationContext(), getResources().getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
      this.finish();
    }
    
    // Init filtered list
    fillFilteredList();
    
    
    // Init the Spinner
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( this, R.array.sort_array, android.R.layout.simple_spinner_item );
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    Spinner sortSpinner = (Spinner) findViewById(R.id.sortSpinner);
    sortSpinner.setAdapter(adapter);
    sortSpinner.setSelection(0);
    sortSpinner.setOnItemSelectedListener(new SortListener());
    
    setListAdapter(new WineAdapter(this, R.layout.list_wine, _wineListFiltered));

    ListView lv = getListView();
    lv.setTextFilterEnabled(true);

    lv.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // When clicked, open the edit screen
        updatedItem = _wineListFiltered[position];
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.DATA_WINE, _wineListFiltered[position]);
        Intent newIntent = new Intent(getApplicationContext(), DisplayScreen.class);
        newIntent.putExtras(bundle);
        startActivityForResult(newIntent, 0);
      }
    });
	}
	
	private void init() {
	  Bundle bundle = this.getIntent().getExtras();
	  Serializable data = bundle.getSerializable(Constants.DATA_WINE_VECTOR);
	  if (data == null || !(data instanceof WineVectorSerializer)) {
	    Toast.makeText(getApplicationContext(), getResources().getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
      this.finish();
	  }
		
	  Vector<Vin> vector = ((WineVectorSerializer) data).getData();
		
		_wineList = new Vin[vector.size()];
		_wineListFiltered = new Vin[vector.size()];
		int index = 0;
		
		for (Vin vin : vector) {
      _wineList[index] = vin;
      _wineListFiltered[index] = vin;
      _IDsMap.put(String.valueOf(vin.getId()), vin);
      _winesMap.put(vin, String.valueOf(vin.getId()));
      index++;
		}
	}
	
	public Object onRetainNonConfigurationInstance() {
	  return new DataStore(_wineList, _IDsMap, _winesMap, updatedItem);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		// Delete success case
		if (resultCode == Constants.RETURN_DELETE_OK) {
			updateAfterDelete();
		}
		else if (resultCode == Constants.RETURN_HOME) {
		  setResult(Constants.RETURN_HOME);
      finish();
		}
		// Update or cancel - the wine may have been modified
		else {
      if (data == null || data.getSerializableExtra(Constants.DATA_WINE) == null || !(data.getSerializableExtra(Constants.DATA_WINE) instanceof Vin)) {
        Toast.makeText(this, getResources().getString(R.string.unexpected_error).toString(), Toast.LENGTH_LONG).show();
        return;
      }
		  updateAfterUpdate((Vin)data.getSerializableExtra(Constants.DATA_WINE));
		}
	}
	
	// Updates the list view when a wine has been updated
	private void updateAfterUpdate(Vin iUpdatedWine) {
    try {
      int updatedId = updatedItem.getId();
      
      int indexUnsorted = 0;
      for (Vin v : _wineList) {
        if (v.getId() == updatedId) {
          break;
        }
        indexUnsorted++;
      }

      // Update the item in the array
      _wineList[indexUnsorted] = iUpdatedWine;
      setListAdapter(new WineAdapter(this, R.layout.list_wine, _wineListFiltered));

      _winesMap.remove(updatedItem);
      _winesMap.put(iUpdatedWine, String.valueOf(updatedId));
      _IDsMap.remove(String.valueOf(updatedId));
      _IDsMap.put(String.valueOf(updatedId), iUpdatedWine);
      fillFilteredList();
      setListAdapter(new WineAdapter(TheWinesApp.getContext(), R.layout.list_wine, _wineListFiltered));
    }
    catch (Exception e) {
      Toast.makeText(getApplicationContext(), getResources().getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
    }
	}
	
	private void updateAfterDelete() {
    if (_wineList.length == 1) {
      //We deleted the only element of the list, we can close this Activity
      this.finish();
    }
	  
    // Update both maps
    String id = String.valueOf(updatedItem.getId());
    _winesMap.remove(updatedItem);
    _IDsMap.remove(id);
    
    // Update the array of Wines
    Vin[] newList = new Vin[_wineList.length -1];
    int index = 0;
    for (Vin vin : _wineList) {
      if (vin.getId() == Integer.parseInt(id)) {
        //Skip this item as it's been deleted
        continue;
      }
      newList[index] = vin;
      index++;
    }
    _wineList = newList.clone();
    fillFilteredList();
    setListAdapter(new WineAdapter(this, R.layout.list_wine, _wineListFiltered));
	}
	
	
	// Menu-related
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.menu.add_search_home_menu, menu);
	  return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	  // Handle item selection
	  if (item.getItemId() == R.id.menu_add) {
	    Intent intent = new Intent(WineListView.this, AddScreen.class);
	    startActivity(intent);
	    finish();
	  }
	  else if (item.getItemId() == R.id.menu_search) {
	    Intent intent = new Intent(WineListView.this, SearchScreen.class);
	    startActivity(intent);
	    finish();
	  }
	  else if (item.getItemId() == android.R.id.home) {
	    finish();
	  }
	  return true;
	}
	// Menu ends
	
	// Contextual menu
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.menu.list_item_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	  updatedItem = _wineListFiltered[info.position];
	  
	  if (item.getItemId() == R.id.menu_display) {
      Bundle bundle = new Bundle();
      bundle.putSerializable(Constants.DATA_WINE, updatedItem);
      Intent newIntent = new Intent(getApplicationContext(), DisplayScreen.class);
      newIntent.putExtras(bundle);
      startActivityForResult(newIntent, Constants.REQUEST_EDIT_FROM_LIST);
	  }
	  else if (item.getItemId() == R.id.menu_edit) {
      Bundle bundle = new Bundle();
      bundle.putSerializable(Constants.DATA_WINE, updatedItem);
      Intent newIntent = new Intent(getApplicationContext(), EditScreen.class);
      newIntent.putExtras(bundle);
      startActivityForResult(newIntent, Constants.REQUEST_EDIT_FROM_LIST);
    }
	  else if (item.getItemId() == R.id.menu_delete) {
	    handleDelete();
    }
	  else if (item.getItemId() == R.id.menu_drink) {
      handleDrink();
    }
	  return true;
	}
  // Contextual menu ends
	
	
	private void handleDelete() {
	  // Show up the confirm dialog
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
	  try {
	    Vin wineToDelete = updatedItem;

	    String picturePath = wineToDelete.getImagePath();
	    int deleteResult = DatabaseAdapter.instance().delete(wineToDelete);
	    
	    if (deleteResult == 1) {
	      Toast.makeText(getApplicationContext(), getResources().getString(R.string.wine_delete_success),Toast.LENGTH_SHORT).show();
	      updateAfterDelete();

	      // Delete the image, if it exists
	      if (picturePath != null && picturePath.length() > 0) {
	        File pictureFile = new File(picturePath);
	        if (pictureFile != null) {
	          if (pictureFile.exists()) {
	            pictureFile.delete();
	            pictureFile = null;
	          }
	        }
	      }
	    }
	    else {
	      Toast.makeText(getApplicationContext(), getResources().getString(R.string.wine_delete_error),Toast.LENGTH_SHORT).show();
	    }
	  }
	  catch (Exception e) {
	    Toast.makeText(getApplicationContext(), getResources().getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
	  }
	}
	
	private void handleDrink() {
	  
	  Vin selectedWine = updatedItem;
	  if (selectedWine != null && selectedWine.getStock() > 0) {
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
      Vin updatedWine = updatedItem;
      if (DatabaseAdapter.instance().setStock(updatedWine.getId(), updatedWine.getStock() -1)) {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.drink_ok), Toast.LENGTH_SHORT).show();
        updatedWine.setStock(updatedWine.getStock() -1);
        updateAfterUpdate(updatedWine);
      }
      else {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
      }
    }
    catch (Exception e) {
      Toast.makeText(getApplicationContext(), getResources().getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
    }
  }
  
  //update & save scroll value on onPause & onResume.
  @Override
  protected void onPause() {
	  super.onPause();
	  scrollY = listView.getFirstVisiblePosition();
	  View v = listView.getChildAt(0);
	  scrollXOffset = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());
  }
  @Override
  protected void onResume() {
	  super.onResume();
	  listView.setSelectionFromTop(scrollY, scrollXOffset);
  }
	 
	 

  private class SortListener implements AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		  filterText.setText(null);
			switch (arg2) {
				case Vin.SORT_APP:
					Vin.sortAccordingTo = Vin.SORT_APP;
					filterText.setEnabled(true);
					break;
				case Vin.SORT_NAME:
					Vin.sortAccordingTo = Vin.SORT_NAME;
					filterText.setEnabled(true);
					break;
				case Vin.SORT_COLOR:
					Vin.sortAccordingTo = Vin.SORT_COLOR;
					filterText.setEnabled(false);
					break;
				case Vin.SORT_YEAR:
					Vin.sortAccordingTo = Vin.SORT_YEAR;
					filterText.setEnabled(true);
					break;
				case Vin.SORT_MARK:
					Vin.sortAccordingTo = Vin.SORT_MARK;
					filterText.setEnabled(false);
					break;
				case Vin.SORT_STOCK:
					Vin.sortAccordingTo = Vin.SORT_STOCK;
					filterText.setEnabled(false);
					break;
				case Vin.SORT_LOCATION:
					Vin.sortAccordingTo = Vin.SORT_LOCATION;
					filterText.setEnabled(true);
					break;
				case Vin.SORT_AGINGLEFT:
          Vin.sortAccordingTo = Vin.SORT_AGINGLEFT;
          filterText.setEnabled(false);
          break;
				default:
					Vin.sortAccordingTo = Vin.SORT_NAME;
					filterText.setEnabled(true);
					break;
			}
			
			// Put all the wines in a List we can sort
			ArrayList<Vin> list = new ArrayList<Vin>(_wineList.length);
			for (Vin v: _wineList) {
				list.add(v);
			}
			Collections.sort(list);
			
			// Put the sorted wines in a new array to relace the old one
			Vin[] sortedWineList = new Vin[list.size()];
			int index = 0;
			for (Vin v: list) {
				sortedWineList[index++] = v;
			}
			_wineList = sortedWineList.clone();
			fillFilteredList();
			setListAdapter(new WineAdapter(TheWinesApp.getContext(), R.layout.list_wine, _wineListFiltered));
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// Nothing to do
		}
		
	}
  
  
  protected class DataStore {
    
    protected Vin[] _dswineList;
    protected HashMap<String, Vin> _dsIDsMap;
    protected HashMap<Vin, String> _dswinesMap;
    protected Vin dsupdatedItem;
    
    public DataStore(Vin[] wineList, HashMap<String, Vin> idMap, HashMap<Vin, String> wineMap, Vin updatedItem) {
      _dswineList = wineList;
      _dsIDsMap = idMap;
      _dswinesMap = wineMap;
      dsupdatedItem = updatedItem;
    }
  }
  
  protected class FilterListener implements OnEditorActionListener {

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
      fillFilteredList();
      return true;
    }
  }
  
  protected void fillFilteredList() {
    // Filter displayed elements
    Vector<Vin> filteredWines = new Vector<Vin>();
    for (Vin vin : _wineList) {
      if (vin.isKeptAfterFilter(filterText.getText().toString())) {
        filteredWines.add(vin);
      }
    }
    
    _wineListFiltered = new Vin[filteredWines.size()];
    int index = 0;
    for (Vin vin : filteredWines) {
      _wineListFiltered[index++] = vin;
    }
    
    setListAdapter(new WineAdapter(this, R.layout.list_wine, _wineListFiltered));
  }
  
}
