package com.devilopers.guigeek.vins;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.guigeek.vins.R;

public class OverviewScreen extends Activity {

  private HashMap<String, WineAggregate> aggregatedWines;
  
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.overview_activity);
    
    initWrapper();
    fillAggregate();
    fillTableRows();
  }

  
  
  private void fillTableRows() {
    
    TableLayout ll = (TableLayout) findViewById(R.id.overview_table);
    boolean fillBackground = true;
    
    for (String colour: aggregatedWines.keySet()) {
      WineAggregate aAggregate = aggregatedWines.get(colour);
      
      if (aAggregate.total == 0) {
        continue;
      }
      
      TableRow row = new TableRow(this);
      
      if (fillBackground) {
        row.setBackgroundColor(0x667d1519);
      }
      else {
        row.setBackgroundColor(0x66999999);
      }
      fillBackground = ! fillBackground;
      
      TextView colourTV = new TextView(this);
      TextView totalTV = new TextView(this);
      TextView nowTV = new TextView(this);
      TextView oneTwoTV = new TextView(this);
      TextView threeFiveTV = new TextView(this);
      TextView fivePlusTV = new TextView(this);
      TextView tenPlusTV = new TextView(this);
      TextView unknownTV = new TextView(this);
      
      TableRow.LayoutParams p = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.FILL_PARENT);
      p.leftMargin = 15;
      totalTV.setLayoutParams(p);
      nowTV.setLayoutParams(p);
      oneTwoTV.setLayoutParams(p);
      threeFiveTV.setLayoutParams(p);
      fivePlusTV.setLayoutParams(p);
      tenPlusTV.setLayoutParams(p);
      unknownTV.setLayoutParams(p);
      
      colourTV.setText(colour);
      totalTV.setText(String.valueOf(aAggregate.total));
      nowTV.setText(String.valueOf(aAggregate.now));
      oneTwoTV.setText(String.valueOf(aAggregate.oneTwo));
      threeFiveTV.setText(String.valueOf(aAggregate.threeFive));
      fivePlusTV.setText(String.valueOf(aAggregate.fivePlus));
      tenPlusTV.setText(String.valueOf(aAggregate.tenPlus));
      unknownTV.setText(String.valueOf(aAggregate.unknown));
      
      row.addView(colourTV);
      row.addView(totalTV);
      row.addView(nowTV);
      row.addView(oneTwoTV);
      row.addView(threeFiveTV);
      row.addView(fivePlusTV);
      row.addView(tenPlusTV);
      row.addView(unknownTV);
      ll.addView(row);
    }
  }



  private int dpToPixel(int dp) {
    float scale = getResources().getDisplayMetrics().density;
    return (int) ((float) dp * scale);
  }



  private void initWrapper() {
    aggregatedWines = new HashMap<String, OverviewScreen.WineAggregate>();
  }



  private void fillAggregate() {
    // Retrieve all the wines in stock
    Vector<Vin> listOfWines = DatabaseAdapter.instance().viewAllInStock();
    
    if (listOfWines.isEmpty()) {
      Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_wine_in_stock), Toast.LENGTH_SHORT).show();
      this.finish();
    }
    
    // Fill the aggregated values
    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    for (Vin vin: listOfWines) {
      
      // Translate the colour
      String colour = translateColour(vin.getColour());
      
      WineAggregate aAggregate = aggregatedWines.get(colour);
      if (aAggregate == null) {
        aAggregate = new WineAggregate();
        aggregatedWines.put(colour, aAggregate);
      }
      
      aAggregate.total++;
      if (vin.getMillesime() > 0 && vin.getAgingPotential() > 0) {
        int targetYear = vin.getMillesime() + vin.getAgingPotential();
        if (targetYear <= currentYear) {
          aAggregate.now++;
        }
        else {
          int left = targetYear - currentYear;
          if (left >= 10) {
            aAggregate.tenPlus++;
          }
          else if (left >= 5) {
            aAggregate.fivePlus++;
          }
          else if (left >= 3) {
            aAggregate.threeFive++;
          }
          else {
            aAggregate.oneTwo++;
          }
        }
      }
      else {
        aAggregate.unknown++;
      }
    }
  }



  private String translateColour(String colour) {

    if (colour.equals(DatabaseAdapter.COLOUR_RED)) {
      return getResources().getString(R.string.colour_red);
    }
    if (colour.equals(DatabaseAdapter.COLOUR_WHITE)) {
      return getResources().getString(R.string.colour_white);
    }
    if (colour.equals(DatabaseAdapter.COLOUR_ROSE)) {
      return getResources().getString(R.string.colour_rose);
    }
    if (colour.equals(DatabaseAdapter.COLOUR_YELLOW)) {
      return getResources().getString(R.string.colour_yellow);
    }
    if (colour.equals(DatabaseAdapter.COLOUR_CHAMPAGNE)) {
      return getResources().getString(R.string.colour_champ);
    }
    if (colour.equals(DatabaseAdapter.COLOUR_FORTIFIED)) {
      return getResources().getString(R.string.colour_fortified);
    }
    
    return colour;
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
      Intent intent = new Intent(OverviewScreen.this, AddScreen.class);
      startActivity(intent);
      finish();
    } else if (item.getItemId() == R.id.menu_search) {
      Intent intent = new Intent(OverviewScreen.this, SearchScreen.class);
      startActivity(intent);
      finish();
    } else if (item.getItemId() == android.R.id.home) {
      finish();
    }
    return true;
  }
  // Menu ends
  
  
  private class WineAggregate {
    public int total = 0, now = 0, oneTwo = 0, threeFive = 0, fivePlus = 0, tenPlus = 0, unknown = 0;
  }

}
