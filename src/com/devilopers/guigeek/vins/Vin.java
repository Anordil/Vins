package com.devilopers.guigeek.vins;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;


public class Vin implements Serializable, Comparable<Vin> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4029222649646413462L;
	private String _name, _appellation, _colour, _cepage="", _accords="", _pointOfSale="", _description="", _imagePath = "";
	private int _millesime, _note, _agingPotential=0, _stock = 0;
	private int _id, _location = 0;
	private double _price=0;
	private byte[] _imageBytes = null;
	private boolean _buyAgain = false;
	
	
	
	public final static int SORT_NAME = 	0;
	public final static int SORT_APP = 		1;
	public final static int SORT_YEAR = 	2;
	public final static int SORT_COLOR = 	3;
	public final static int SORT_MARK = 	4;
	public final static int SORT_STOCK = 	5;
	public final static int SORT_LOCATION = 6;
	public static int sortAccordingTo = SORT_NAME;
	
	
	// Minimal constructor
	public Vin(String nom, String appellation, String colour, int millesime, int note) {
		setNom(nom);
		setAppellation(appellation);
		setMillesime(millesime);
		setNote(note);
		setColour(colour);
	}
	
	// All fields
	public Vin(String nom, String appellation, String colour, int millesime, int note, String cepage, String accords, String pos, int agingPotential, 
	           double price, int stock, String description, String imagePath, int id, int loca, boolean buyAgain) {
		setNom(nom);
		setAppellation(appellation);
		setMillesime(millesime);
		setNote(note);
		setColour(colour);
		_cepage = cepage;
		_accords = accords;
		_pointOfSale = pos;
		_agingPotential = agingPotential;
		_price = price;
		setStock(stock);
		setDescription(description);
		setImagePath(imagePath);
		setId(id);
		setLocation(loca);
		setBuyAgain(buyAgain);
	}
	


	public void setNom(String _nom) {
		this._name = _nom;
	}

	public String getNom() {
		return _name;
	}

	public void setAppellation(String _appellation) {
		this._appellation = _appellation;
	}

	public String getAppellation() {
		return _appellation;
	}

	public void setMillesime(int _millesime) {
		this._millesime = _millesime;
	}

	public int getMillesime() {
		return _millesime;
	}

	public void setNote(int _note) {
		this._note = _note;
	}

	public int getNote() {
		return _note;
	}

	public void setColour(String _colour) {
		this._colour = _colour;
	}

	public String getColour() {
		return _colour;
	}

	public void setCepage(String _cepage) {
		this._cepage = _cepage;
	}

	public String getCepage() {
		return _cepage;
	}

	public void setAccords(String _accords) {
		this._accords = _accords;
	}

	public String getAccords() {
		return _accords;
	}

	public void setPointOfSale(String _pointOfSale) {
		this._pointOfSale = _pointOfSale;
	}

	public String getPointOfSale() {
		return _pointOfSale;
	}

	public void setAgingPotential(int _agingPotential) {
		this._agingPotential = _agingPotential;
	}

	public int getAgingPotential() {
		return _agingPotential;
	}

	public void setPrice(double _prix) {
		this._price = _prix;
	}

	public double getPrice() {
		return _price;
	}

	public void setDescription(String _description) {
		this._description = _description;
	}

	public String getDescription() {
		return _description;
	}

	public void setStock(int _stock) {
		this._stock = _stock;
	}

	public int getStock() {
		return _stock;
	}

	public void setId(int _id) {
    this._id = _id;
  }

  public int getId() {
    return _id;
  }

  public void setImagePath(String _imagePath) {
    this._imagePath = _imagePath;
  }

  public String getImagePath() {
    return _imagePath;
  }

  @Override
	public int compareTo(Vin other) {
		int result = 0;
		switch (sortAccordingTo) {
		
		// Sort by name then year
		case SORT_NAME:
			result = this._name.compareToIgnoreCase(other._name);
			if (result == 0) {
				result = this._millesime - other._millesime;
			}
			return result;
		
		// Sort by appellation, then name, then year
		case SORT_APP:
			result = this._appellation.compareToIgnoreCase(other._appellation);
			if (result == 0) {
				result = this._name.compareToIgnoreCase(other._name);
			}
			if (result == 0) {
				result = this._millesime - other._millesime;
			}
			return result;
		
		// Vintage, then name
		case SORT_YEAR:
			result = this._millesime - other._millesime;
			if (result == 0) {
				result = this._name.compareToIgnoreCase(other._name);
			}
			return result;
		
		// Color then name then year
		case SORT_COLOR:
			result = this._colour.compareToIgnoreCase(other._colour);
			if (result == 0) {
				result = this._name.compareToIgnoreCase(other._name);
			}
			if (result == 0) {
				result = this._millesime - other._millesime;
			}
			return result;
		
		// Score (descending !), then name, then year
		case SORT_MARK:
			result = -this._note + other._note;
			if (result == 0) {
				result = this._name.compareToIgnoreCase(other._name);
			}
			if (result == 0) {
				result = this._millesime - other._millesime;
			}
			return result;
		
		// Nb of bottles, then appellation, then name, then year
		case SORT_STOCK:
			result = -this._stock + other._stock;
			if (result == 0) {
				result = this._appellation.compareToIgnoreCase(other._appellation);
			}
			if (result == 0) {
				result = this._name.compareToIgnoreCase(other._name);
			}
			if (result == 0) {
				result = this._millesime - other._millesime;
			}
			return result;
			
		case SORT_LOCATION:
			String nameThis = DatabaseAdapter.instance().getCompartmentLongName(this.getLocation());
			String nameOther = DatabaseAdapter.instance().getCompartmentLongName(other.getLocation());
			result = nameThis.compareToIgnoreCase(nameOther);
			if (result == 0) {
				result = this._millesime - other._millesime;
			}
			return result;
		
		// Default: name then year
		default:
			result = this._name.compareToIgnoreCase(other._name);
			if (result == 0) {
				result = this._millesime - other._millesime;
			}
			return result;
		}
	}

  public boolean isKeptAfterFilter(String filter) {
    
    if (filter == null || filter.equals("null") || filter.length() == 0) {
      return true;
    }
    
    switch (sortAccordingTo) {
      case SORT_APP:
        return _appellation.toUpperCase().contains(filter.toUpperCase());
  
      case SORT_NAME:
        return _name.toUpperCase().contains(filter.toUpperCase());
  
      case SORT_YEAR:
        return String.valueOf(_millesime).contains(filter);
        
      case SORT_LOCATION:
    	  return DatabaseAdapter.instance().getCompartmentLongName(this.getLocation()).toUpperCase().contains(filter.toUpperCase());
  
      default:
        return true;
    }
  }

  public int getLocation() {
    return _location;
  }
  public void setLocation(int iLoc) {
    _location = iLoc;
  }
  
  
  public boolean isBuyAgain() {
	return _buyAgain;
}

public void setBuyAgain(boolean _buyAgain) {
	this._buyAgain = _buyAgain;
}

// Read the image from the SD card
  public void loadImage() {
	  set_imageBytes(null);
	  if (_imagePath != null && _imagePath.length() > 0) {
		  
		  File aImageFile = new File(_imagePath);
		  
		  // Just to get the image size
		  BitmapFactory.Options options = new BitmapFactory.Options();
		  options.inJustDecodeBounds = true;
		  BitmapFactory.decodeFile(aImageFile.getAbsolutePath(), options);
		  
		  // No more than screen width
		  int smallEdge = Math.min(options.outHeight, options.outWidth);
		  Display aScreen = ((WindowManager)TheWinesApp.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		  int screenWidth = Math.min(aScreen.getWidth(), aScreen.getHeight());
		  
		  int ratio = 1;
		  if (smallEdge > 0 && screenWidth > 0) {
		    ratio = smallEdge/screenWidth;
	      
	      // Ratio has to be a power of 2
	      if (ratio > 0 && smallEdge/ratio < screenWidth -100) { 
	        ratio /= 2;
	      }
		  }
		  
		  
		  options.inJustDecodeBounds = false;
		  options.inSampleSize = ratio;
		  Bitmap aBitmap = BitmapFactory.decodeFile(aImageFile.getAbsolutePath(), options);
		  if (aBitmap != null) {
			  ByteArrayOutputStream stream = new ByteArrayOutputStream();
			  boolean result = aBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
			  
			  if (!result) {
			  }
			  
			  set_imageBytes(stream.toByteArray());
			  try {
				  stream.close();
			  } catch (IOException e) {
			  }
		  }
	  }
  }
  
  // Delete the image to free memory
  public void freeImage() {
	  set_imageBytes(null);
  }

public byte[] get_imageBytes() {
	return _imageBytes;
}

public void set_imageBytes(byte[] _imageBytes) {
	this._imageBytes = _imageBytes;
}


}
