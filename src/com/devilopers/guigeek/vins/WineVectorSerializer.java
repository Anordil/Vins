package com.devilopers.guigeek.vins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

import android.os.Environment;
import android.widget.Toast;

import com.guigeek.vins.R;

public class WineVectorSerializer implements Serializable {

  private static final long serialVersionUID = 12L;
  private Vector<Vin> data;
  
  public WineVectorSerializer(Vector<Vin> inputData) {
    data = new Vector<Vin>();
    for (Vin vin : inputData) {
      data.add(vin);
    }
  }
  
  
  public WineVectorSerializer(boolean fillWithWholeDB) {
    data = new Vector<Vin>();
    if (fillWithWholeDB) {
      fillWithWholeDB();
    }
  }


  public Vector<Vin> getData() {
    return data;
  }
  
  
  public void fillWithWholeDB() {
    data.clear();
    Vector<Vin> allDB = DatabaseAdapter.instance().getAll();
    for (Vin vin : allDB) {
      data.add(vin);
    }
  }
  
  public String doExport(String iFileName) throws IOException {

	  FileOutputStream fos = null;
	  ObjectOutputStream out = null;

	  File rootFolder = Environment.getExternalStorageDirectory();
	  File filename = new File(rootFolder, iFileName);

	  fos = new FileOutputStream(filename);
	  out = new ObjectOutputStream(fos);
	  
	  // Serialize all the wines
	  for (Vin vin: data) {
		  out.writeObject(vin);
		  out.flush();
	  }
	  out.close();
	  
	  return filename.getAbsolutePath();
  }
  
}
