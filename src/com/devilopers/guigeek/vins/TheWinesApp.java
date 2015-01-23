package com.devilopers.guigeek.vins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.guigeek.vins.R;

public class TheWinesApp extends Activity implements OnClickListener {

  private Button _buttonAdd, _buttonSearch, _buttonList, _buttonStock;
  private static Context context;

  public static final String WRAPPER = "WRAPPER";

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    context = getApplicationContext();
    setContentView(R.layout.main);

    _buttonAdd = (Button) findViewById(R.id.mainmenu_add);
    _buttonSearch = (Button) findViewById(R.id.mainmenu_search);
    _buttonList = (Button) findViewById(R.id.mainmenu_list);
    _buttonStock = (Button) findViewById(R.id.mainmenu_stock);

    _buttonAdd.setOnClickListener(this);
    _buttonSearch.setOnClickListener(this);
    _buttonList.setOnClickListener(this);
    _buttonStock.setOnClickListener(this);

    // Display updates
    SharedPreferences prefs = getSharedPreferences(Constants.PREF, MODE_PRIVATE);
    if (prefs.getBoolean(Constants.PREF_UPDATE_LOCATION, true)) {
      prefs.edit().putBoolean(Constants.PREF_UPDATE_LOCATION, false).commit();
      
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage(getResources().getString(R.string.update_dialog_location))
      .setCancelable(false)
      .setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          dialog.cancel();
        }
      });
      AlertDialog alert = builder.create();
      alert.show();
    }
  }

  @Override
  public void onClick(View v) {
    if (v == _buttonAdd) {
      Intent intent = new Intent(TheWinesApp.this, AddScreen.class);
      startActivity(intent);
    }
    else if (v == _buttonSearch) {
      Intent intent = new Intent(TheWinesApp.this, SearchScreen.class);
      startActivity(intent);
    }
    else if (v == _buttonList) {
      // Display all wines
      Vector<Vin> listOfWines = DatabaseAdapter.instance().getAll();
      if (listOfWines.size() > 0) {
        Toast.makeText(getApplicationContext(), listOfWines.size() + " " + getResources().getString(R.string.x_matches), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(TheWinesApp.this, WineListView.class);

        WineVectorSerializer aBundle = new WineVectorSerializer(listOfWines);
        intent.putExtra(Constants.DATA_WINE_VECTOR, aBundle);

        startActivity(intent);
      }
      else {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_wine_in_db), Toast.LENGTH_SHORT).show();
      }
    }
    else if (v == _buttonStock) {
      // Display all wines where stock > 0
      Vector<Vin> listOfWines = DatabaseAdapter.instance().viewAllInStock();
      if (listOfWines.size() > 0) {
        Toast.makeText(getApplicationContext(), listOfWines.size() + " " + getResources().getString(R.string.x_matches), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(TheWinesApp.this, WineListView.class);
        WineVectorSerializer aBundle = new WineVectorSerializer(listOfWines);
        intent.putExtra(Constants.DATA_WINE_VECTOR, aBundle);
        startActivity(intent);
      }
      else {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_wine_in_stock), Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void doImport() {
    Intent intent = new Intent(TheWinesApp.this, FilePicker.class);
    intent.setAction(Intent.ACTION_MAIN);
    startActivity(intent);
  }

  public static Context getContext() {
    return context;
  }

  private void doExport() {
    WineVectorSerializer wrapper = new WineVectorSerializer(true);
    FileOutputStream fos = null;
    ObjectOutputStream out = null;

    File rootFolder = Environment.getExternalStorageDirectory();
    File filename = new File(rootFolder, getResources().getString(R.string.filename));

    try {
      fos = new FileOutputStream(filename);
      out = new ObjectOutputStream(fos);
      out.writeObject(wrapper);
      out.close();
      Toast.makeText(getApplicationContext(), getResources().getString(R.string.export_success)  + " " + filename.getAbsolutePath(), Toast.LENGTH_LONG).show();
    } catch (IOException ex) {
      ex.printStackTrace();
      Toast.makeText(getApplicationContext(), getResources().getString(R.string.export_error), Toast.LENGTH_SHORT).show();
    }
  }


  // Menu-related
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    if (item.getItemId() == R.id.menu_prefs) {
      Intent intent = new Intent(TheWinesApp.this, PreferencesManager.class);
      startActivity(intent);
    }
    else if (item.getItemId() == R.id.menu_export) {
      //Display a warning
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage(getResources().getString(R.string.confirm_export_message))
      .setCancelable(false)
      .setPositiveButton(getResources().getString(R.string.confirm_delete_yes), new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          doExport();
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
    else if (item.getItemId() == R.id.menu_import) {
      doImport();
    }
    return true;
  }
  // Menu ends



}
