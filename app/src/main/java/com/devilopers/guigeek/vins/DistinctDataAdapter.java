package com.devilopers.guigeek.vins;

import java.util.Set;
import java.util.TreeSet;

import android.app.Activity;
import android.widget.ArrayAdapter;

public class DistinctDataAdapter {
  
  private Activity mParent;

  public DistinctDataAdapter(Activity parent) {
    mParent = parent;
  }

  /**
   * Selects the distinct values for the input column in DB, and returns an adapter to a sorted array of those values.
   * Returns null if no values exist.
   * @param iColumn the name of the column to use, must be one of DatabaseAdapter.KEY_*
   */
  public ArrayAdapter<String> getAdapter(String iColumn) {
    String[] aValues = DatabaseAdapter.instance().getDistinctData(iColumn);
    if (aValues.length == 0) {
      return null;
    }
    return new ArrayAdapter<String>(mParent, android.R.layout.simple_list_item_1, aValues);
  }

  // Same as the above, only each distinct value then needs to be split into tokens
  public ArrayAdapter<String> getAdapter(String iColumn, boolean needsTokenizing) {
    
    if (!needsTokenizing) {
      return getAdapter(iColumn);
    }
    
    String[] aValues = DatabaseAdapter.instance().getDistinctData(iColumn);
    if (aValues.length == 0) {
      return null;
    }
    
    Set<String> setOfUniqueTokens = new TreeSet<String>();
    for (String severalTokens: aValues) {
      String[] tokens = severalTokens.split(", ");
      for (String token: tokens) {
        setOfUniqueTokens.add(token);
      }
    }
    
    String[] finalArray = new String[setOfUniqueTokens.size()];
    int index = 0;
    for (String token: setOfUniqueTokens) {
      finalArray[index++] = token;
    }
    
    return new ArrayAdapter<String>(mParent, android.R.layout.simple_list_item_1, finalArray);
  }
}
