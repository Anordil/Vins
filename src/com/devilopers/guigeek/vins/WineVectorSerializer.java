package com.devilopers.guigeek.vins;

import java.io.Serializable;
import java.util.Vector;

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
  
}
