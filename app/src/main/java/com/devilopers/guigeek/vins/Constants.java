package com.devilopers.guigeek.vins;

public class Constants {
  
  //Request codes
  public static final int REQUEST_EDIT_FROM_LIST = 100;
  public static final int REQUEST_PICTURE = 101;
  public static final int REQUEST_BROWSE_PHOTO = 102;
  public static final int REQUEST_SELECT_LOCATION = 1023;
  
  // Return codes
  public static final int RETURN_HOME = 200;
  public static final int RETURN_DELETE_OK = 201;
  public static final int RETURN_CANCEL = 202;
  
  // Intent data
  public static final String DATA_WINE = "com.guigeek.wine.singlewine";
  public static final String DATA_WINE_VECTOR = "com.guigeek.wine.winevector";
  public static final String DATA_SECTION = "com.guigeek.wine.section";
  public static final String DATA_COMP_ID = "com.guigeek.wine.compID";
  
  
  // Preferences
  public static final String PREF = "prefwines";
  public static final String PREF_PICTURE = "prefpicture";
  public static final String PREF_AGING = "prefaging";
  public static final String PREF_STOCK = "prefstock";
  public static final String PREF_POS_AND_PRICE = "prefposandprice";
  public static final String PREF_VARIETY = "prefvariety";
  public static final String PREF_ASSESSMENTS = "prefassessments";
  public static final String PREF_BEST_WITH = "prefbestservedwith";
  public static final String PREF_UPDATE_LOCATION = "prefdisplayupdatelocation";
  public static final String PREF_UPDATE_HOURGLASS = "prefdisplayupdatehourglass";

}
