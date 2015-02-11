package com.devilopers.guigeek.vins;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter extends SQLiteOpenHelper {

  public static final String 	KEY_ID = "id";

  public static final String 	KEY_APPELLATION = "appellation";
  public static final String 	KEY_NOM = "nom";
  public static final String 	KEY_MILLESIME = "millesime";
  public static final String 	KEY_CEPAGE = "cepage";
  public static final String 	KEY_NOTE = "note";
  public static final String 	KEY_PRIX = "prix";
  public static final String 	KEY_ACCORDS = "accords";
  public static final String 	KEY_DESCRIPTION = "description";
  public static final String 	KEY_COLOUR = "couleur";
  public static final String 	KEY_POINT_OF_SALE = "lieu";
  public static final String 	KEY_AGING_POTENTIAL = "garde";
  public static final String 	KEY_STOCK = "stock";
  public static final String  KEY_IMAGE = "image";
  public static final String  KEY_LOCATION = "location";
  

  public static final String 	MILLESIME_COMPARATOR = "comp-mill";
  public static final String 	PRICE_COMPARATOR = "comp-prix";
  public static final String 	STOCK_COMPARATOR = "comp-stock";

  public static final String COLOUR_RED = "rouge";
  public static final String COLOUR_WHITE = "blanc";
  public static final String COLOUR_ROSE = "rose";
  public static final String COLOUR_YELLOW = "jaune";
  public static final String COLOUR_CHAMPAGNE = "champagne";
  public static final String COLOUR_FORTIFIED = "fortified";
  public static final String COLOUR_ANY = "";

  private static final String TAG = "DBAdapter";

  private static final String DATABASE_NAME = "WineSorter";
  private static final String DTNAME_WINES = "vins";
  private static final int DATABASE_VERSION = 8;
  
  private static final String DTNAME_SECTION = "sections";
  private static final String DTNAME_COMPARTMENT = "compartments";
  public static final String  KEY_PARENT_SECTION = "parent";

  public static final int DBWINE_COL_NB = 15;

  private static final String DATABASE_CREATE =
    "create table " + 	DTNAME_WINES + " (" 
    + KEY_ID + 			" integer primary key autoincrement,"
    + KEY_NOM + 		" text not null,"
    + KEY_APPELLATION + " text,"
    + KEY_COLOUR + 		" text default " + COLOUR_RED + " not null,"
    + KEY_CEPAGE + 		" text,"
    + KEY_ACCORDS + 	" text,"
    + KEY_DESCRIPTION + " text,"
    + KEY_MILLESIME + 	" int not null,"
    + KEY_NOTE + 		" int,"
    + KEY_PRIX + 		" double,"
    + KEY_POINT_OF_SALE + 			" text,"
    + KEY_AGING_POTENTIAL + 		" int,"
    + KEY_STOCK + 		" int default 0 not null,"
    + KEY_IMAGE + " text,"
    + KEY_LOCATION +     " int default 0"
    + ");";
  
  private static final String DBCREATE_SECTION =
    "create table " + DTNAME_SECTION + " (" 
    + KEY_ID +      " integer primary key autoincrement,"
    + KEY_NOM +     " text not null"
    + ");";
  private static final String DBCREATE_COMPARTMENT =
    "create table " + DTNAME_COMPARTMENT + " (" 
    + KEY_ID +      " integer primary key autoincrement,"
    + KEY_NOM +     " text not null,"
    + KEY_PARENT_SECTION +     " int not null"
    + ");";

  private static DatabaseAdapter instance = null;



  private DatabaseAdapter(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(DATABASE_CREATE);
    db.execSQL(DBCREATE_SECTION);
    db.execSQL(DBCREATE_COMPARTMENT);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    //Passage de la version 3 à 4 : ajout du lieu d'achat et de la période de garde
    Log.w(TAG, " Upgrading database from version " + oldVersion + " to "+ newVersion);
    switch(oldVersion) {
    case 3:
      updateDBVersion3(db);
      updateDBVersion4(db);
      updateDBVersion5(db);
      updateDBVersion6(db);
      updateDBVersion7(db);
      break;
    case 4:
      updateDBVersion4(db);
      updateDBVersion5(db);
      updateDBVersion6(db);
      updateDBVersion7(db);
      break;
    case 5:
      updateDBVersion5(db);
      updateDBVersion6(db);
      updateDBVersion7(db);
      break;
    case 6:
      updateDBVersion6(db);
      updateDBVersion7(db);
      break;
    case 7:
      updateDBVersion7(db);
      break;
    default:
      db.execSQL("DROP TABLE IF EXISTS " + DTNAME_WINES);
      onCreate(db);
      break;
    }
  }

  private void updateDBVersion7(SQLiteDatabase db) {
    // Ajouter une nouvelle colonne "Location" pour les vins, créer les deux tables section et compartment
    db.execSQL("ALTER TABLE " + DTNAME_WINES + " ADD COLUMN "  + KEY_LOCATION + " int");
    db.execSQL(DBCREATE_SECTION);
    db.execSQL(DBCREATE_COMPARTMENT);
  }
  private void updateDBVersion6(SQLiteDatabase db) {
    // Ajouter une nouvelle colonne "Image" pour gérer les photos des bouteilles
    db.execSQL("ALTER TABLE " + DTNAME_WINES + " ADD COLUMN "  + KEY_IMAGE + " text");
  }

  private void updateDBVersion5(SQLiteDatabase db) {
    // Ajouter une nouvelle colonne "Stock" pour gérer les quantités. Valeur par défaut 0, non null.
    db.execSQL("ALTER TABLE " + DTNAME_WINES + " ADD COLUMN "  + KEY_STOCK + " int default 0 not null");
  }

  private void updateDBVersion4(SQLiteDatabase db) {
    // Changer les "null" en "" pour les lieux de vente
    db.execSQL("UPDATE " + DTNAME_WINES + " SET " + KEY_POINT_OF_SALE + "=\"\"" + " WHERE " + KEY_POINT_OF_SALE + " IS NULL");
  }

  private void updateDBVersion3(SQLiteDatabase db) {
    // Ajouter deux colonnes :
    // lieu -> text
    // durée de garde -> int
    db.execSQL("ALTER TABLE " + DTNAME_WINES + " ADD COLUMN "  + KEY_POINT_OF_SALE + " text");
    db.execSQL("ALTER TABLE " + DTNAME_WINES + " ADD COLUMN "  + KEY_AGING_POTENTIAL + " int");
  }

  public static DatabaseAdapter instance() {
    if ( instance == null) {
      instance = new DatabaseAdapter(TheWinesApp.getContext());
    }
    return instance;
  }

  // Returns the row number if the insert was successful, -1 if it failed because of an error, and -2 if it failed because the same item already exists in DB
  public long addEntry(String nom, String appellation, String couleur, String cepage, String accords, String description, int millesime, int note, double prix, 
      String lieu, int garde, int quantity, String imagePath, int compartmentId)
  {
    
    SQLiteDatabase db = getWritableDatabase();

    //In the select, ' are not automatically escaped !
    String wAppellation = appellation.replace("'", "''");
    String wNom = nom.replace("'", "''");

    // First let's check if a wine with same name/appellation/millesime/colour exists. If yes, don't perform the insert
    String where = KEY_APPELLATION + "=='" + wAppellation + "' AND " + KEY_NOM + "=='" + wNom 
                   + "' AND " + KEY_MILLESIME + "==" + millesime + " AND " + KEY_COLOUR + "=='" + couleur + "'";

    Cursor theSelectCursor = db.query(DTNAME_WINES, null, where, null, null, null, null);
    if (theSelectCursor.moveToFirst()) {
      theSelectCursor.close();
      return -2;
    }
    theSelectCursor.close();

    //Values for the insert statement
    ContentValues values = new ContentValues(DBWINE_COL_NB);
    values.put(KEY_NOM, nom);
    values.put(KEY_APPELLATION, appellation);
    values.put(KEY_COLOUR, couleur);
    values.put(KEY_CEPAGE, cepage);
    values.put(KEY_ACCORDS, accords);
    values.put(KEY_DESCRIPTION, description);
    values.put(KEY_MILLESIME, millesime);
    values.put(KEY_NOTE, note);
    values.put(KEY_PRIX, (String) (prix !=0 ? prix + "" : ""));
    values.put(KEY_POINT_OF_SALE, lieu);
    values.put(KEY_AGING_POTENTIAL, (String) (garde !=0 ? garde + "" : ""));
    values.put(KEY_STOCK, quantity);
    values.put(KEY_IMAGE, imagePath == null ? "" : imagePath);
    values.put(KEY_LOCATION, compartmentId);
    
    long result = db.insert(DTNAME_WINES, null, values);
    db.close();

    return result;
  }

  public boolean updateEntry(Vin vin) {
    
    SQLiteDatabase db = getWritableDatabase();
    db.beginTransaction();

    ContentValues values = new ContentValues(DBWINE_COL_NB);
    values.put(KEY_NOM, vin.getNom());
    values.put(KEY_APPELLATION, vin.getAppellation());
    values.put(KEY_COLOUR, vin.getColour());
    values.put(KEY_CEPAGE, vin.getCepage());
    values.put(KEY_ACCORDS, vin.getAccords());
    values.put(KEY_DESCRIPTION, vin.getDescription());
    values.put(KEY_MILLESIME, vin.getMillesime());
    values.put(KEY_NOTE, vin.getNote());
    values.put(KEY_PRIX, (String) (vin.getPrice() !=0 ? vin.getPrice() + "" : ""));
    values.put(KEY_POINT_OF_SALE, vin.getPointOfSale());
    values.put(KEY_AGING_POTENTIAL, (String) (vin.getAgingPotential() !=0 ? vin.getAgingPotential() + "" : ""));
    values.put(KEY_STOCK, vin.getStock());
    values.put(KEY_IMAGE, vin.getImagePath() == null ? "" : vin.getImagePath());
    values.put(KEY_LOCATION, vin.getLocation() > 0 ? vin.getLocation() : 0);

    int result = db.update(DTNAME_WINES, values, KEY_ID + "=" + vin.getId(), null);
    if (result != 1) {
      db.endTransaction();
      db.close();
      return false;
    }
    db.setTransactionSuccessful();
    db.endTransaction();
    db.close();
    return true;
  }

  public Vector<Vin> search(ContentValues values) {

    SQLiteDatabase db = getReadableDatabase();

    String[] selectionArgs = 
    {"'" + (values.containsKey(KEY_NOM) ? "%" + values.getAsString(KEY_NOM) : "") + "%'",
        "'" + (values.containsKey(KEY_APPELLATION)? "%" + values.getAsString(KEY_APPELLATION) : "") + "%'",
        "'" + (values.containsKey(KEY_CEPAGE) ? "%" + values.getAsString(KEY_CEPAGE) : "") + "%'",
        "'" + (values.containsKey(KEY_ACCORDS) ? "%" + values.getAsString(KEY_ACCORDS) : "") + "%'",
        values.containsKey(KEY_MILLESIME) ? values.getAsString(KEY_MILLESIME) : "-1",
            values.containsKey(KEY_PRIX) ? values.getAsString(KEY_PRIX) : "-1",
                "'" + (values.containsKey(KEY_POINT_OF_SALE) ? "%" + values.getAsString(KEY_POINT_OF_SALE) : "") + "%'",
                values.containsKey(KEY_STOCK) ? values.getAsString(KEY_STOCK) : "-1",
    };

    String operatorForPrice = values.containsKey(PRICE_COMPARATOR) ? values.getAsString(PRICE_COMPARATOR) : ">=",
           operatorForYear  = values.containsKey(MILLESIME_COMPARATOR) ? values.getAsString(MILLESIME_COMPARATOR) : ">=",
           operatorForStock = values.containsKey(STOCK_COMPARATOR) ? values.getAsString(STOCK_COMPARATOR) : ">=";

    // change < into <=, = into == and > into >=
    if (operatorForPrice.length() == 1) {
      operatorForPrice += "=";
    }
    if (operatorForYear.length() == 1) {
      operatorForYear += "=";
    }
    if (operatorForStock.equals("=")) {
      operatorForStock += "=";
    }
  
    // Filter by colour
    String colourWhere = "";
    if (values.getAsBoolean(COLOUR_RED)) {
    	colourWhere += KEY_COLOUR + "=" + "'" + COLOUR_RED + "'";
    }
    if (values.getAsBoolean(COLOUR_ROSE)) {
    	if (colourWhere.length() > 0) {colourWhere += " OR ";}
    	colourWhere += KEY_COLOUR + "=" + "'" + COLOUR_ROSE + "'";
    }
    if (values.getAsBoolean(COLOUR_YELLOW)) {
    	if (colourWhere.length() > 0) {colourWhere += " OR ";}
    	colourWhere += KEY_COLOUR + "=" + "'" + COLOUR_YELLOW + "'";
    }
    if (values.getAsBoolean(COLOUR_WHITE)) {
    	if (colourWhere.length() > 0) {colourWhere += " OR ";}
    	colourWhere += KEY_COLOUR + "=" + "'" + COLOUR_WHITE + "'";
    }
    if (values.getAsBoolean(COLOUR_CHAMPAGNE)) {
    	if (colourWhere.length() > 0) {colourWhere += " OR ";}
    	colourWhere += KEY_COLOUR + "=" + "'" + COLOUR_CHAMPAGNE + "'";
    }
    if (values.getAsBoolean(COLOUR_FORTIFIED)) {
    	if (colourWhere.length() > 0) {colourWhere += " OR ";}
    	colourWhere += KEY_COLOUR + "=" + "'" + COLOUR_FORTIFIED + "'";
    }
    if (colourWhere.length() == 0) {
    	colourWhere = KEY_COLOUR + " LIKE '%'";
    }
  
  
    String where = KEY_NOM + " LIKE " + selectionArgs[0] + " AND " + KEY_APPELLATION + " LIKE " + selectionArgs[1]
                   + " AND " + KEY_CEPAGE + " LIKE " + selectionArgs[2] + " AND " + KEY_ACCORDS + " LIKE " + selectionArgs[3] 
                   + " AND " + KEY_POINT_OF_SALE + " LIKE " + selectionArgs[6] + " AND " + KEY_MILLESIME + " " + operatorForYear 
                   + " " + selectionArgs[4] + " AND " + KEY_PRIX + " " + operatorForPrice + " " + selectionArgs[5]  + " AND " + KEY_STOCK 
                   + " " + operatorForStock + " " + selectionArgs[7] + " AND (" + colourWhere + ")";
    
    Cursor cursor = db.query(DTNAME_WINES, null, where, null, null, null, KEY_NOM + " ASC, " + KEY_MILLESIME + " ASC");
    Vector<Vin> result = cursorToWineVector(cursor);
    cursor.close();
    db.close();
    return result;
  }

  public int delete(Vin vin) {
    SQLiteDatabase db = getWritableDatabase();
    int result = db.delete(DTNAME_WINES, KEY_ID + "==" + vin.getId(), null);
    db.close();
    return result;
  }

  public Vector<Vin> getAll() {
    SQLiteDatabase db = getReadableDatabase();
    String sql = "SELECT * from " + DTNAME_WINES + " ORDER BY " + KEY_NOM + " ASC, " + KEY_MILLESIME + " ASC";
    
    Cursor cursor = db.rawQuery(sql, null);
    Vector<Vin> result = cursorToWineVector(cursor);
    cursor.close();
    db.close();
    return result;
  }

  // Used to display the list of all wines in stock (stock > 0).
  public Vector<Vin> viewAllInStock() {
    SQLiteDatabase db = getReadableDatabase();
    String sql = "SELECT * from " + DTNAME_WINES + " WHERE " + KEY_STOCK + " > 0 ORDER BY " + KEY_NOM + " ASC, " + KEY_MILLESIME + " ASC";

    Cursor cursor = db.rawQuery(sql, null);
    Vector<Vin> result = cursorToWineVector(cursor);
    cursor.close();
    db.close();
    return result;
  }
  
  public Vector<Section> getSections() {
    Vector<Section> result = new Vector<Section>();
    
    SQLiteDatabase db = getReadableDatabase();
    String sql = "SELECT * from " + DTNAME_SECTION;
    Cursor cursor = db.rawQuery(sql, null);
    
    while (cursor.moveToNext()) {
      result.add(new Section(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_NOM)), cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_ID))));
    }
    
    cursor.close();
    db.close();
    
    return result;
  }
  
  public Vector<Section> getCompartments(int iSectionId) {
    Vector<Section> result = new Vector<Section>();
    
    SQLiteDatabase db = getReadableDatabase();
    String sql = "SELECT * from " + DTNAME_COMPARTMENT + " WHERE " + KEY_PARENT_SECTION + "=" + iSectionId;
    Cursor cursor = db.rawQuery(sql, null);
    
    while (cursor.moveToNext()) {
      result.add(new Section(cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_NOM)), cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_ID))));
    }
    
    cursor.close();
    db.close();
    
    return result;
  }
  
  // Used to display the list of all wines in a given compartment
  public Vector<Vin> viewAllInCompartment(int iCompId) {
    SQLiteDatabase db = getReadableDatabase();
    String sql = "SELECT * from " + DTNAME_WINES + " WHERE " + KEY_LOCATION + "=" + iCompId + " ORDER BY " + KEY_NOM + " ASC, " + KEY_MILLESIME + " ASC";

    Cursor cursor = db.rawQuery(sql, null);
    Vector<Vin> result = cursorToWineVector(cursor);
    cursor.close();
    db.close();
    return result;
  }
  
  // Used to display the list of all wines in a given section
  public Vector<Vin> viewAllInSection(int iSectionId) {
    
    // Get all the compartments in the input section
    SQLiteDatabase db = getReadableDatabase();
    String sql = "SELECT " + KEY_ID + " from " + DTNAME_COMPARTMENT + " WHERE " + KEY_PARENT_SECTION + "=" + iSectionId;
    
    // Add all wines to the returned vector
    Vector<Vin> result = new Vector<Vin>();
    
    Cursor cursor = db.rawQuery(sql, null);
    if (cursor.isClosed()) {
      return result;
    }
    while (cursor.moveToNext()) {
      Vector<Vin> winesFromComp = viewAllInCompartment(cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_ID)));
      for (Vin v: winesFromComp) {
        result.add(v);
      }
    }
    cursor.close();
    db.close();
    return result;
  }
  


  public String[] getDistinctData(String column) {
    Set<String> dataVector = new HashSet<String>();
    String[] aStringArray = {};

    
    SQLiteDatabase db = getReadableDatabase();
    String sql = "SELECT DISTINCT " + column + " from " + DTNAME_WINES + " ORDER BY " + column + " ASC";
    Cursor cursor = db.rawQuery(sql, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
    	String data = cursor.getString(cursor.getColumnIndex(column));
    	
    	// Remove percentage and change to camel case
    	String[] comaSet = data.split(",", 0);
    	for (int j = 0; j < comaSet.length; j++) {
        	String[] splitSet = comaSet[j].split(" ", 0);
        	String token = "";
        	for (int i = 0 ; i < splitSet.length; i++) {
        		String part = splitSet[i].trim();
        		if (!part.contains("%") && !part.matches("\\d") && part.length() > 1) {
        			if (token.length() > 0) token += " ";
        			token += part.substring(0, 1).toUpperCase(Locale.US) + part.substring(1).toLowerCase(Locale.US);
        		}
        		else {
        			dataVector.add(token.replaceAll(",", ""));
        			token = "";
        		}
        	}
        	if (token.length() > 0) {
        		dataVector.add(token.replaceAll(",", ""));
        	}
    	}
    	
    	if (column.equals(KEY_APPELLATION)) {
    		for (int k = 0; k < StaticData.appellations.length; k++) {
    			dataVector.add(StaticData.appellations[k]);
    		}
    	}
    	else if (column.equals(KEY_CEPAGE)) {
    		for (int k = 0; k < StaticData.cepages.length; k++) {
    			dataVector.add(StaticData.cepages[k]);
    		}
    	}
    	
    	cursor.moveToNext();
    }
    cursor.close();
    
    

    if (dataVector.size() > 0) {
      aStringArray = new String[dataVector.size()];
      int i = 0;
      for (String token: dataVector) {
        aStringArray[i++] = token;
      }
    }
    db.close();
    return aStringArray;
  }

  public boolean setStock(int id, int newStock) {
    
    SQLiteDatabase db = getWritableDatabase();
    db.beginTransaction();

    ContentValues values = new ContentValues(1);
    values.put(KEY_STOCK, newStock);

    int result = db.update(DTNAME_WINES, values, KEY_ID + "=" + id, null);
    if (result != 1) {
      db.endTransaction();
      db.close();
      return false;
    }
    db.setTransactionSuccessful();
    db.endTransaction();
    db.close();
    return true;
  }
  
  private Vector<Vin> cursorToWineVector(Cursor queryResult) {
    if (queryResult.isClosed()) {
      return null;
    }
    
    Vector<Vin> allWines = new Vector<Vin>();
    
    while (queryResult.moveToNext()) {
      allWines.add(new Vin( queryResult.getString(queryResult.getColumnIndex(DatabaseAdapter.KEY_NOM)), 
                            queryResult.getString(queryResult.getColumnIndex(DatabaseAdapter.KEY_APPELLATION)), 
                            queryResult.getString(queryResult.getColumnIndex(DatabaseAdapter.KEY_COLOUR)), 
                            queryResult.getInt(queryResult.getColumnIndex(DatabaseAdapter.KEY_MILLESIME)), 
                            queryResult.getInt(queryResult.getColumnIndex(DatabaseAdapter.KEY_NOTE)),
                            queryResult.getString(queryResult.getColumnIndex(DatabaseAdapter.KEY_CEPAGE)),
                            queryResult.getString(queryResult.getColumnIndex(DatabaseAdapter.KEY_ACCORDS)),
                            queryResult.getString(queryResult.getColumnIndex(DatabaseAdapter.KEY_POINT_OF_SALE)),
                            queryResult.getInt(queryResult.getColumnIndex(DatabaseAdapter.KEY_AGING_POTENTIAL)),
                            queryResult.getDouble(queryResult.getColumnIndex(DatabaseAdapter.KEY_PRIX)),
                            queryResult.getInt(queryResult.getColumnIndex(DatabaseAdapter.KEY_STOCK)),
                            queryResult.getString(queryResult.getColumnIndex(DatabaseAdapter.KEY_DESCRIPTION)),
                            queryResult.getString(queryResult.getColumnIndex(DatabaseAdapter.KEY_IMAGE)),
                            queryResult.getInt(queryResult.getColumnIndex(DatabaseAdapter.KEY_ID)),
                            queryResult.getInt(queryResult.getColumnIndex(DatabaseAdapter.KEY_LOCATION))
      )
      );
    }
    return allWines;
  }

  public long createSection(String name) {
    
    SQLiteDatabase db = getWritableDatabase();


    // First let's check if a section with this name exists
    String wName = name.replace("'", "''");
    String where = KEY_NOM + "=='" + wName + "'";

    Cursor theSelectCursor = db.query(DTNAME_SECTION, null, where, null, null, null, null);
    if (theSelectCursor.moveToFirst()) {
      theSelectCursor.close();
      return -2;
    }
    theSelectCursor.close();

    //Values for the insert statement
    ContentValues values = new ContentValues(1);
    values.put(KEY_NOM, name);

    long result = db.insert(DTNAME_SECTION, null, values);
    db.close();

    return result;
  }

  public long createCompartment(String name, int id) {
    
    SQLiteDatabase db = getWritableDatabase();


    // First let's check if a comp with this name exists
    String wNom = name.replace("'", "''");
    String where = KEY_NOM + "=='" + wNom + "' AND " + KEY_PARENT_SECTION + "==" + id;

    Cursor theSelectCursor = db.query(DTNAME_COMPARTMENT, null, where, null, null, null, null);
    if (theSelectCursor.moveToFirst()) {
      theSelectCursor.close();
      return -2;
    }
    theSelectCursor.close();

    //Values for the insert statement
    ContentValues values = new ContentValues(2);
    values.put(KEY_NOM, name);
    values.put(KEY_PARENT_SECTION, id);

    long result = db.insert(DTNAME_COMPARTMENT, null, values);
    db.close();

    return result;
  }

  public String getCompartmentLongName(int compId) {
    SQLiteDatabase db = getWritableDatabase();
    String result = "";
    
    if (compId <= 0) {
    	return "";
    }

    // Get the compartment
    String where = KEY_ID + "==" + compId;
    Cursor theCompCursor = db.query(DTNAME_COMPARTMENT, null, where, null, null, null, null);
    
    if (theCompCursor.moveToFirst()) {
      result += theCompCursor.getString(theCompCursor.getColumnIndex(DatabaseAdapter.KEY_NOM));
      int sectionId = theCompCursor.getInt(theCompCursor.getColumnIndex(DatabaseAdapter.KEY_PARENT_SECTION));
      
      String whereSec = KEY_ID + "==" + sectionId;
      Cursor theSecCursor = db.query(DTNAME_SECTION, null, whereSec, null, null, null, null);
      if (theSecCursor.moveToFirst()) {
        result = theSecCursor.getString(theSecCursor.getColumnIndex(DatabaseAdapter.KEY_NOM)) + ", " + result;
      }
      theSecCursor.close();
    }
    theCompCursor.close();
    db.close();
    
    return result;
  }

  public boolean deleteCompartment(int id) {
    SQLiteDatabase db = getWritableDatabase();
    int result = db.delete(DTNAME_COMPARTMENT, KEY_ID + "==" + id, null);
    db.close();
    return result > 0;
  }

  public boolean deleteSection(int id) {
    SQLiteDatabase db = getWritableDatabase();
    int result = db.delete(DTNAME_SECTION, KEY_ID + "==" + id, null);
    db.delete(DTNAME_COMPARTMENT, KEY_PARENT_SECTION + "==" + id, null);
    db.close();
    return result > 0;
  }
}
